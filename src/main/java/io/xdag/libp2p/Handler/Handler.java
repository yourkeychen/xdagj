package io.xdag.libp2p.Handler;

import com.google.common.util.concurrent.*;
import io.libp2p.core.Connection;
import io.libp2p.core.P2PChannel;
import io.libp2p.core.multistream.ProtocolBinding;
import io.libp2p.core.multistream.ProtocolDescriptor;
import io.netty.channel.ChannelHandlerContext;

import io.xdag.Kernel;
import io.xdag.core.Block;
import io.xdag.core.BlockWrapper;
import io.xdag.core.Blockchain;
import io.xdag.libp2p.Libp2pChannel;
import io.xdag.libp2p.Node;
import io.xdag.libp2p.manager.Libp2pChannelManager;
import io.xdag.libp2p.message.MessageQueueLib;
import io.xdag.net.XdagVersion;
import io.xdag.net.handler.MessageCodes;
import io.xdag.net.message.AbstractMessage;
import io.xdag.net.message.Message;
import io.xdag.net.message.NetStatus;
import io.xdag.net.message.impl.*;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Hex;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.xdag.config.Constants.REQUEST_BLOCKS_MAX_TIME;

@Slf4j
public class Handler implements ProtocolBinding<Handler.Xdag05> {

    Kernel kernel;
    Libp2pChannelManager libp2pChannelManager;
    Xdag05 xdag05;
    Libp2pChannel libp2pChannel;
    XdagBlockHandler xdagBlockHandler;
    public Handler() {
    }

    public Handler(Kernel kernel) {
        this.kernel = kernel;
        libp2pChannelManager = kernel.getLibp2pChannelManager();
    }

    @NotNull
    @Override
    public ProtocolDescriptor getProtocolDescriptor() {
        return  new ProtocolDescriptor("xdagj");
    }


    @NotNull
    @Override
    public CompletableFuture<Xdag05> initChannel(@NotNull P2PChannel p2PChannel, @NotNull String s) {
        log.info("initChannel");
        final Connection connection = ((io.libp2p.core.Stream) p2PChannel).getConnection();
        final Node node = new Node(connection.secureSession().getRemoteId());
        libp2pChannel = new Libp2pChannel(connection,this);
        libp2pChannel.init();
        xdag05 = new Xdag05(node, libp2pChannel);
        libp2pChannelManager.add(libp2pChannel);
        xdagBlockHandler = new XdagBlockHandler(libp2pChannel);
        xdagBlockHandler.setMessageFactory(new Xdag03MessageFactory());
        xdagBlockHandler.setMsgQueue(new MessageQueueLib(libp2pChannel));
        if (!p2PChannel.isInitiator()) {
            log.info("p2PChannel is not Initiator");
        }

        MessageCodes messageCodes = new MessageCodes();
        p2PChannel.pushHandler(xdagBlockHandler);
        p2PChannel.pushHandler(messageCodes);
        p2PChannel.pushHandler(xdag05);

        return xdag05.activeFuture;


    }

    public synchronized void dropConnection() throws InterruptedException {

        xdag05.dropConnection();
        if(libp2pChannel != null){
            libp2pChannelManager.remove(libp2pChannel);
        }
    }
    public void sendMessage(Message msg){
        this.xdag05.sendMessage(msg);
    }



    static class Xdag05 extends libXdagHandler {
        private static final ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger cnt = new AtomicInteger(0);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "sendThread-" + cnt.getAndIncrement());
            }
        };
        final Node node;
        final Libp2pChannel libp2pChannel;

        protected final CompletableFuture<Xdag05> activeFuture = new CompletableFuture<>();
        ExecutorService sendThreads = new ScheduledThreadPoolExecutor(1, factory);
        List<ListenableFuture<Integer>> futures = new ArrayList<>();
        public Xdag05(Node node, Libp2pChannel libp2pChannel) {
            this.node = node;
            this.libp2pChannel = libp2pChannel;
            this.blockchain = kernel.getBlockchain();
            this.syncMgr = kernel.getSyncMgr();
        }
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws InterruptedException {
            log.debug("接收到新消息 in xdag03：" + msg.getCommand());
            msgQueue.receivedMessage(msg);

            switch (msg.getCommand()) {
                case NEW_BLOCK:
                    processNewBlock((NewBlockMessage) msg);
                    break;
                case BLOCK_REQUEST:
                    processBlockRequest((BlockRequestMessage) msg);
                    break;
                case BLOCKS_REQUEST:
                    processBlocksRequest((BlocksRequestMessage) msg);
                    break;
                case BLOCKS_REPLY:
                    processBlocksReply((BlocksReplyMessage) msg);
                    break;
                case SUMS_REQUEST:
                    processSumRequest((SumRequestMessage) msg);
                    break;
                case SUMS_REPLY:
                    processSumReply((SumReplyMessage) msg);
                    break;
                case BLOCKEXT_REQUEST:
                    processBlockExtRequest();
                    break;
                default:
                    break;
            }
        }
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            // 这里的ctx是最后一个handler的
            msgQueue.activate(ctx);
        }
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws InterruptedException {
            log.debug("channel inactive:[{}] ", ctx.toString());
            this.killTimers();
            disconnect();
        }
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws InterruptedException {
            log.debug("Xdag handling failed");
            ctx.close();
            killTimers();
            disconnect();
        }
        protected synchronized void processBlocksReply(BlocksReplyMessage msg) {
            log.debug("Process BlocksReply:" + msg);
            updateNetStatus(msg);
        }
        /** 将sumrequest的后8个字段填充为自己的sum 修改type类型为reply 发送 */
        protected synchronized void processSumRequest(SumRequestMessage msg) {
            updateNetStatus(msg);
            // byte[] sum = new byte[256];
            byte[] sum = kernel.getBlockStore().getSimpleFileStore().loadSum(msg.getStarttime(), msg.getEndtime());
            SumReplyMessage reply = new SumReplyMessage(msg.getEndtime(), msg.getRandom(), kernel.getNetStatus(), sum);
            sendMessage(reply);
        }

        protected synchronized void processSumReply(SumReplyMessage msg) {
            log.debug("Process SumReply " + msg);
            updateNetStatus(msg);
            SettableFuture<SumReplyMessage> future = futureSumSublist.poll();
            assert future != null;
            future.set(msg);
        }

        protected synchronized void processBlockExtRequest() {
        }
        protected synchronized void processBlocksRequest(BlocksRequestMessage msg) {
            log.debug("Process BlocksRequest:" + msg);
            updateNetStatus(msg);
            long starttime = msg.getStarttime();
            long endtime = msg.getEndtime();
            long random = msg.getRandom();
            ListenableFuture<Integer> future = MoreExecutors.listeningDecorator(sendThreads)
                    .submit(new SendTask(blockchain, starttime, endtime));
            futures.add(future);
            Futures.addCallback(
                    future,
                    new FutureCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            if (integer == 1) {
                                sendMessage(new BlocksReplyMessage(integer, endtime, random, kernel.getNetStatus()));
                            } else {
                                log.debug("出现问题");
                            }
                            futures.remove(future);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            log.debug("发送失败");
                        }
                    },
                    MoreExecutors.directExecutor());
            log.debug("futures size:" + futures.size());
        }

        private void processBlockRequest(BlockRequestMessage msg) {
            log.debug("Process Blockrequest:" + msg);
            byte[] find = new byte[32];
            byte[] hash = msg.getHash();
            hash = Arrays.reverse(hash);
            System.arraycopy(hash, 8, find, 8, 24);
            Block block = blockchain.getBlockByHash(find, true);
            if (block != null) {
                NewBlockMessage message = new NewBlockMessage(block, kernel.getConfig().getTTL());
                sendMessage(message);
            }
        }

        @Override
        public void sendNewBlock(Block newBlock, int TTL) {
            log.debug("Send block hash " + Hex.toHexString(newBlock.getHashLow()));
            NewBlockMessage msg = new NewBlockMessage(newBlock, TTL);
            sendMessage(msg);
        }

        @Override
        public void sendGetblocks(long starttime, long endtime) {
            BlocksRequestMessage msg = new BlocksRequestMessage(starttime, endtime, kernel.getNetStatus());

            sendMessage(msg);
        }

        @Override
        public void sendGetblock(byte[] hash) {
            BlockRequestMessage blockRequestMessage = new BlockRequestMessage(hash, kernel.getNetStatus());

            sendMessage(blockRequestMessage);
        }

        @Override
        public ListenableFuture<SumReplyMessage> sendGetsums(long starttime, long endtime) {
            log.debug("SendGetSums starttime " + starttime + " endtime " + endtime);
            if (endtime - starttime <= REQUEST_BLOCKS_MAX_TIME) {
                // 发送getblock请求
                sendGetblocks(starttime, endtime);
                return null;
            } else {
                // 依旧发送sum请求
                SumRequestMessage msg = new SumRequestMessage(starttime, endtime, kernel.getNetStatus());
                SettableFuture<SumReplyMessage> future = SettableFuture.create();
                futureSumSublist.offer(future);

                sendMessage(msg);
                return future;
            }
        }

        @Override
        public synchronized void dropConnection() throws InterruptedException {
            log.info("Peer {}: is a bad one, drop", channel.getIp());
            disconnect();
        }


        protected void disconnect() throws InterruptedException {
            msgQueue.disconnect();
            if (sendThreads != null) {
                if (futures.size() != 0) {
                    for (ListenableFuture<Integer> future : futures) {
                        future.cancel(true);
                    }
                }
                sendThreads.shutdown();
                sendThreads.awaitTermination(5, TimeUnit.SECONDS);
            }
        }
        @Override
        public boolean isIdle() {
            return false;
        }

        @Override
        public BigInteger getTotalDifficulty() {
            return null;
        }

        @Override
        public void activate() {
            log.debug("Xdag protocol activate");
        }

        @Override
        public XdagVersion getVersion() {
            return null;
        }

        @Override
        public void disableBlocks() {

        }

        @Override
        public void enableBlocks() {

        }

        @Override
        public void onSyncDone(boolean done) {

        }

        @Override
        public void sendMessage(Message message) {
            if (msgQueue.isRunning()) {
                msgQueue.sendMessage(message);
            } else {
                log.debug("msgQueue is close");
            }
        }
        public void killTimers() {
            log.debug("msgqueue stop");
            msgQueue.close();
        }
        /** *********************** Message Processing * *********************** */
        protected synchronized void processNewBlock(NewBlockMessage msg) throws InterruptedException {
            Block block = msg.getBlock();
            log.debug("New block received: block.index [{}]", block.toString());
            log.debug("Block data:" + Hex.toHexString(block.getXdagBlock().getData()));
            log.debug("ttl:" + msg.getTtl());
            //todo:BlockWrapper
            if (!syncMgr.validateAndAddNewBlock(new BlockWrapper(block, msg.getTtl() - 1, libp2pChannel.getNode()))) {
                dropConnection();
            }
        }
        public void updateNetStatus(AbstractMessage message) {
            updateNetS(message, kernel);
        }

        public static void updateNetS(AbstractMessage message, Kernel kernel) {
            NetStatus remoteNetStatus = message.getNetStatus();
            log.debug("Remote netstatus:" + remoteNetStatus);
            synchronized (kernel.getNetStatus()) {
                kernel.getNetStatus().updateNetStatus(remoteNetStatus);
            }
            synchronized (kernel.getNetDBMgr()) {
                log.debug("update netdb");
                kernel.getNetDBMgr().updateNetDB(message.getNetDB());
            }
        }

        class SendTask implements Callable<Integer> {
            private final Blockchain blockchain;
            private final long starttime;
            private final long endtime;

            public SendTask(Blockchain blockchain, long starttime, long endtime) {
                this.blockchain = blockchain;
                this.starttime = starttime;
                this.endtime = endtime;
            }

            @Override
            public Integer call() {
                if (blockchain == null) {
                    return 1;
                }
                List<Block> blocks = blockchain.getBlockByTime(starttime, endtime);
                if (blocks == null || blocks.size() == 0) {
                    log.debug("Nothing to send");
                    return 1;
                }
                for (Block block : blocks) {
                    sendNewBlock(block, 1);
                }
                return 1;
            }
        }
    }
}
