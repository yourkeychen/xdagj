/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2030 The XdagJ Developers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.xdag;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.xdag.config.Config;
import io.xdag.consensus.SyncManager;
import io.xdag.consensus.XdagPow;
import io.xdag.consensus.XdagSync;
import io.xdag.core.Block;
import io.xdag.core.Blockchain;
import io.xdag.core.BlockchainImpl;
import io.xdag.core.XdagState;
import io.xdag.db.DatabaseFactory;
import io.xdag.db.DatabaseName;
import io.xdag.db.rocksdb.RocksdbFactory;
import io.xdag.db.store.AccountStore;
import io.xdag.db.store.BlockStore;
import io.xdag.db.store.OrphanPool;
import io.xdag.event.KernelBootingEvent;
import io.xdag.event.PubSub;
import io.xdag.event.PubSubFactory;
import io.xdag.libp2p.Libp2pChannel;
import io.xdag.libp2p.jvmLibp2pNetwork;
import io.xdag.libp2p.manager.Libp2pChannelManager;
import io.xdag.mine.MinerServer;
import io.xdag.mine.handler.ConnectionLimitHandler;
import io.xdag.mine.manager.AwardManager;
import io.xdag.mine.manager.AwardManagerImpl;
import io.xdag.mine.manager.MinerManager;
import io.xdag.mine.manager.MinerManagerImpl;
import io.xdag.mine.miner.Miner;
import io.xdag.mine.miner.MinerStates;
import io.xdag.net.XdagClient;
import io.xdag.net.XdagServer;
import io.xdag.net.XdagVersion;
import io.xdag.net.manager.NetDBManager;
import io.xdag.net.manager.XdagChannelManager;
import io.xdag.net.message.MessageQueue;
import io.xdag.net.message.NetDB;
import io.xdag.net.message.NetStatus;
import io.xdag.net.node.NodeManager;
import io.xdag.utils.XdagTime;
import io.xdag.wallet.Wallet;
import io.xdag.wallet.WalletImpl;

public class Kernel {
    private static final PubSub pubSub = PubSubFactory.getDefault();
    protected State state = State.STOPPED;
    protected Status status = Status.STOPPED;
    protected Config config;
    protected Wallet wallet;
    protected DatabaseFactory dbFactory;
    protected BlockStore blockStore;
    protected AccountStore accountStore;
    protected OrphanPool orphanPool;
    protected Blockchain chain;
    protected NetStatus netStatus;
    protected NetDB netDB;
    protected XdagClient client;
    protected XdagChannelManager channelMgr;
    protected NodeManager nodeMgr;
    protected NetDBManager netDBMgr;
    protected XdagServer p2p;
    protected XdagSync sync;
    protected XdagPow pow;
    protected SyncManager syncMgr;
    /** 初始化一个后续都可以用的handler */
    protected ConnectionLimitHandler connectionLimitHandler;
    protected Block firstAccount;
    protected Miner poolMiner;
    protected AwardManager awardManager;
    protected MinerManager minerManager;
    protected MinerServer minerServer;
    protected XdagState xdagState;
    protected jvmLibp2pNetwork jvmLibp2pNetwork;
    protected Libp2pChannelManager libp2pChannelManager;
    protected AtomicInteger channelsAccount = new AtomicInteger(0);


    public Kernel(Config config, Wallet wallet) {
        this.config = config;
        this.wallet = wallet;
        // 启动的时候就是在初始化
        this.xdagState = XdagState.INIT;
    }

    public Kernel(Config config) {
        this.config = config;
    }

    /** Start the kernel. */
    public synchronized void testStart() throws Exception {
        if (state != State.STOPPED) {
            return;
        } else {
            state = State.BOOTING;
            pubSub.publish(new KernelBootingEvent());
        }

        // ====================================
        // print system info
        // ====================================
        System.out.println(
                "Xdag Server system booting up: network = "
                        + (Config.MainNet ? "MainNet" : "TestNet")
                        + ", version "
                        + XdagVersion.V03 + "(base Xdagger V0.3.1)"
                        + ", user host = ["
                        + config.getNodeIp()
                        + ":"
                        + config.getNodePort()
                        + "]");

        // try {
        // //初始密钥
        // config.initKeys();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        // ====================================
        // start channel manager
        // ====================================
        channelMgr = new XdagChannelManager(this);
        netDBMgr = new NetDBManager(config);
        libp2pChannelManager = new Libp2pChannelManager();
//        libp2pChannelManager = new Libp2pChannelManager(this);
        netDBMgr.init();
//        jvmLibp2pNetwork = new jvmLibp2pNetwork(this);
//        jvmLibp2pNetwork.start();
        // ====================================
        // wallet init
        // ====================================
        if (wallet == null) {
            wallet = new WalletImpl();
            wallet.init(config);
        }

        dbFactory = new RocksdbFactory(config);
        blockStore = new BlockStore(
                dbFactory.getDB(DatabaseName.INDEX),
                dbFactory.getDB(DatabaseName.BLOCK),
                dbFactory.getDB(DatabaseName.TIME),
                dbFactory.getSumsDB());
        blockStore.init();
        accountStore = new AccountStore(wallet, blockStore, dbFactory.getDB(DatabaseName.ACCOUNT));
        accountStore.init();
        orphanPool = new OrphanPool(dbFactory.getDB(DatabaseName.ORPHANIND));
        orphanPool.init();

        // ====================================
        // netstatus netdb init
        // ====================================
        netStatus = new NetStatus();
        netDB = new NetDB();

        // ====================================
        // initialize blockchain database
        // ====================================
        chain = new BlockchainImpl(this, dbFactory);
        // 如果是第一次启动，则新建第一个地址块
        if (chain.getAllAccount().size() == 0) {
            firstAccount = new Block(XdagTime.getCurrentTimestamp(), null, null, null, false, null, -1);
            firstAccount.signOut(wallet.getDefKey().ecKey);
            chain.tryToConnect(firstAccount);
            poolMiner = new Miner(firstAccount.getHash());
        } else {
            poolMiner = new Miner(accountStore.getGlobalMiner());
        }

        // log.debug("Net Status:"+netStatus);

        // ====================================
        // set up client
        // ====================================
        p2p = new XdagServer(this);
        p2p.start();
        client = new XdagClient(config);

        // ====================================
        // start node manager
        // ====================================
        nodeMgr = new NodeManager(this);
        nodeMgr.start();

        // ====================================
        // send request
        // ====================================
        sync = new XdagSync(this);
        sync.start();

        // ====================================
        // sync block
        // ====================================
        syncMgr = new SyncManager(this);
        syncMgr.start();

        // ====================================
        // set up pool miner
        // ====================================
        poolMiner.setMinerStates(MinerStates.MINER_SERVICE);

        // ====================================
        // set up minermanager awardmanager
        // ====================================
        minerManager = new MinerManagerImpl(this);
        awardManager = new AwardManagerImpl(this);
        // ====================================
        // poolnode open
        // ====================================
        connectionLimitHandler = new ConnectionLimitHandler(config.getMaxConnectPerIp());
        minerServer = new MinerServer(this);

        // ====================================
        // pow
        // ====================================
        pow = new XdagPow(this);
        minerManager.setPoW(pow);
        minerManager.start();
        if (Config.MainNet) {
            xdagState.setState(XdagState.WAIT);
        } else {
            xdagState.setState(XdagState.WTST);
        }
        Launcher.registerShutdownHook("kernel", this::testStop);
        state = State.RUNNING;
    }

    /** Stops the kernel. */
    public synchronized void testStop() {
        if (state != State.RUNNING) {
            return;
        } else {
            state = State.STOPPING;
            System.out.println("try to shut down the program");
        }

        // stop consensus
        sync.stop();
        syncMgr.stop();
        pow.stop();

        // stop node manager and channel manager
        channelMgr.stop();
        nodeMgr.stop();

        // close timer
        MessageQueue.timer.shutdown();

        // close server
        p2p.close();
        // close client
        client.close();

        ReentrantReadWriteLock.WriteLock lock = chain.getStateLock().writeLock();
        lock.lock();
        try {
            for (DatabaseName name : DatabaseName.values()) {
                dbFactory.getDB(name).close();
            }
            // close save sums
            blockStore.closeSum();
        } finally {
            lock.unlock();
        }

        minerServer.close();
        minerManager.close();
        state = State.STOPPED;
    }

    /**
     * Returns the kernel state.
     *
     * @return
     */
    public State state() {
        return state;
    }

    /**
     * Returns the wallet.
     *
     * @return
     */
    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    /**
     * Returns the blockchain.
     *
     * @return
     */
    public Blockchain getBlockchain() {
        return chain;
    }

    public void setBlockchain(Blockchain chain) {
        this.chain = chain;
    }

    /**
     * Returns the client.
     *
     * @return
     */
    public XdagClient getClient() {
        return client;
    }

    /**
     * Returns the channel manager.
     *
     * @return
     */

    public Libp2pChannelManager getLibp2pChannelManager(){
        return libp2pChannelManager;
    }
    public XdagChannelManager getChannelManager() {
        return channelMgr;
    }

    /**
     * Returns the config.
     *
     * @return
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Returns the p2p server instance.
     *
     * @return a {@link XdagServer} instance or null
     */
    public XdagServer getP2p() {
        return p2p;
    }

    /**
     * Returns the database factory.
     *
     * @return
     */
    public DatabaseFactory getDbFactory() {
        return dbFactory;
    }

    /**
     * Returns blockStore
     *
     * @return
     */
    public BlockStore getBlockStore() {
        return blockStore;
    }

    public void setBlockStore(BlockStore blockStore) {
        this.blockStore = blockStore;
    }

    /**
     * Returns accountStore
     *
     * @return
     */
    public AccountStore getAccountStore() {
        return accountStore;
    }

    public void setAccountStore(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    /**
     * Returns OrphanPool
     *
     * @return
     */
    public OrphanPool getOrphanPool() {
        return orphanPool;
    }

    public void setOrphanPool(OrphanPool orphanPool) {
        this.orphanPool = orphanPool;
    }

    /**
     * Returns NetStatus
     *
     * @return
     */
    public NetStatus getNetStatus() {
        return netStatus;
    }

    public void setNetStatus(NetStatus netStatus) {
        this.netStatus = netStatus;
    }

    /**
     * Returns SyncManager
     *
     * @return
     */
    public SyncManager getSyncMgr() {
        return syncMgr;
    }

    public void setSyncMgr(SyncManager syncMgr) {
        this.syncMgr = syncMgr;
    }

    /**
     * Returns NodeManager
     *
     * @return
     */
    public NodeManager getNodeMgr() {
        return nodeMgr;
    }

    public void setNodeMgr(NodeManager nodeMgr) {
        this.nodeMgr = nodeMgr;
    }

    /**
     * Returns PoW
     *
     * @return
     */
    public XdagPow getPow() {
        return pow;
    }

    public void setPow(XdagPow pow) {
        this.pow = pow;
    }

    /**
     * Returns NetDB
     *
     * @return
     */
    public NetDB getNetDB() {
        return netDB;
    }

    public void setNetDB(NetDB netDB) {
        this.netDB = netDB;
    }

    /**
     * Returns State
     *
     * @return
     */
    public State getState() {
        return state;
    }

    public NetDBManager getNetDBMgr() {
        return netDBMgr;
    }

    public void setNetDBMgr(NetDBManager netDBMgr) {
        this.netDBMgr = netDBMgr;
    }

    public void onSyncDone() {
        status = Status.SYNCDONE;
    }

    public void onPow() {
        status = Status.BLOCK_PRODUCTION_ON;
    }

    public Block getFirstAccount() {
        return firstAccount;
    }

    public void resetStore() {
    }

    public Miner getPoolMiner() {
        return poolMiner;
    }

    public MinerManager getMinerManager() {
        return minerManager;
    }

    public AwardManager getAwardManager() {
        return awardManager;
    }

    public MinerServer getMinerServer() {
        return minerServer;
    }

    public ConnectionLimitHandler getConnectionLimitHandler() {
        return connectionLimitHandler;
    }

    public XdagState getXdagState() {
        return this.xdagState;
    }

    public AtomicInteger getChannelsAccount() {
        return channelsAccount;
    }

    public enum State {
        STOPPED, BOOTING, RUNNING, STOPPING
    }

    public enum Status {
        STOPPED, SYNCING, BLOCK_PRODUCTION_ON, SYNCDONE
    }
}
