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
package io.xdag.mine.manager;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import io.xdag.Kernel;
import io.xdag.consensus.PoW;
import io.xdag.consensus.Task;
import io.xdag.mine.MinerChannel;
import io.xdag.mine.miner.Miner;
import io.xdag.mine.miner.MinerStates;
import io.xdag.net.message.Message;
import io.xdag.utils.ByteArrayWrapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MinerManagerImpl implements MinerManager {
    /** 保存活跃的channel */
    protected Map<InetSocketAddress, MinerChannel> activateMinerChannels = new ConcurrentHashMap<>();

    /** 根据miner的地址保存的数组 activate 代表的是一个已经注册的矿工 */
    protected Map<ByteArrayWrapper, Miner> activateMiners = new ConcurrentHashMap<>(200);

    private Task currentTask = null;

    @Setter
    private PoW poW;
    private Kernel kernel;
    private ScheduledExecutorService server = new ScheduledThreadPoolExecutor(3, new BasicThreadFactory.Builder()
            .namingPattern("MinerManagerThread")
            .daemon(true)
            .build());

    private ScheduledFuture<?> updateFuture;
    private ScheduledFuture<?> cleanChannelFuture;
    private ScheduledFuture<?> cleanMinerFuture;

    public MinerManagerImpl(Kernel kernel) {
        this.kernel = kernel;
    }

    /** 启动 函数 开启遍历和server */
    @Override
    public void start() {
        updateFuture = server.scheduleAtFixedRate(this::updataBalance, 10, 10, TimeUnit.SECONDS);
        cleanChannelFuture = server.scheduleAtFixedRate(this::cleanUnactivateChannel, 64, 32, TimeUnit.SECONDS);
        cleanMinerFuture = server.scheduleAtFixedRate(this::cleanUnactivateMiner, 64, 32, TimeUnit.SECONDS);
    }

    private void updataBalance() {
        try {
            for (MinerChannel channel : activateMinerChannels.values()) {
                if (channel.isActive()) {
                    // log.debug("给channel发送余额");
                    channel.sendBalance();
                }
            }
        } catch (Exception e) {
            log.warn("update balance error");
            e.printStackTrace();
        }
    }

    @Override
    public void addActivateChannel(MinerChannel channel) {
        log.debug("add a new active channel");
        // 一般来讲 地址可能相同 但是端口不同
        activateMinerChannels.put(channel.getInetAddress(), channel);
    }

    @Override
    public void close() {
        if (updateFuture != null) {
            updateFuture.cancel(true);
        }
        if (cleanChannelFuture != null) {
            cleanChannelFuture.cancel(true);
        }
        if (cleanMinerFuture != null) {
            cleanMinerFuture.cancel(true);
        }
        if (server != null) {
            server.shutdown();
        }
        closeMiners();
    }

    private void closeMiners() {
        // 关闭所有连接
        for (MinerChannel channel : activateMinerChannels.values()) {
            channel.dropConnection();
        }
    }

    @Override
    public void removeUnactivateChannel(MinerChannel channel) {
        if (!channel.isActive()) {
            log.debug("remove a channel");
            activateMinerChannels.remove(channel.getInetAddress(), channel);
            Miner miner = activateMiners.get(new ByteArrayWrapper(channel.getAccountAddressHash()));
            miner.removeChannel(channel.getInetAddress());
            miner.subChannelCounts();
            kernel.getChannelsAccount().getAndDecrement();
            if (miner.getConnChannelCounts() == 0) {
                miner.setMinerStates(MinerStates.MINER_ARCHIVE);
            }
        }
    }

    /** 清除当前所有不活跃的channel */
    public void cleanUnactivateChannel() {
        for (MinerChannel channel : activateMinerChannels.values()) {
            removeUnactivateChannel(channel);
        }
    }

    /** 清理minger */
    public void cleanUnactivateMiner() {
        for (Miner miner : activateMiners.values()) {
            if (miner.canRemove()) {
                log.debug("remove a miner");
                activateMiners.remove(new ByteArrayWrapper(miner.getAddressHash()));
            }
        }
    }

    /** 每一轮任务刚发出去的时候 会用这个跟新所有miner的额情况 */
    @Override
    public void updateNewTaskandBroadcast(Task task) {
        currentTask = task;
        for (MinerChannel channel : activateMinerChannels.values()) {
            if (channel.isActive()) {

                channel.setTaskIndex(currentTask.getTaskIndex());
                if (channel.getMiner().getTaskTime() < currentTask.getTaskTime()) {
                    channel.getMiner().setTaskTime(currentTask.getTaskTime());
                }
                channel.sendTaskToMiner(currentTask.getTask());
                channel.setSharesCounts(0);
            }
        }
    }

    @Override
    public Map<ByteArrayWrapper, Miner> getActivateMiners() {
        return activateMiners;
    }

    @Override
    public void onNewShare(MinerChannel channel, Message msg) {
        if (currentTask.getTaskIndex() == channel.getTaskIndex()) {
            poW.receiveNewShare(channel, msg);
        }
    }

    @Override
    public MinerChannel getChannelByHost(InetSocketAddress host) {
        return this.activateMinerChannels.get(host);
    }

    @Override
    public Map<InetSocketAddress, MinerChannel> getActivateMinerChannels() {
        return this.activateMinerChannels;
    }
}
