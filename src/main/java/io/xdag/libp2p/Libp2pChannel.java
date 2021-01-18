package io.xdag.libp2p;

import io.libp2p.core.Connection;
import io.xdag.core.BlockWrapper;
import io.xdag.libp2p.Handler.Handler;
import io.xdag.libp2p.message.Libp2pMessageQueue;
import io.xdag.net.handler.Xdag;
import io.xdag.net.handler.XdagAdapter;
import lombok.extern.slf4j.Slf4j;
import org.spongycastle.util.encoders.Hex;

@Slf4j
public class Libp2pChannel {
    private Connection connection;
    private boolean isActive;
    private boolean isDisconnected = false;
    private Node remotenode;
    private Libp2pMessageQueue messageQueue;
    Handler handler;
    Xdag xdag = new XdagAdapter();

    public Libp2pChannel(Connection connection,Handler handler) {
        this.connection = connection;
        this.handler = handler;
    }


    //存放连接的节点地址
    public void init(){
        remotenode = new Node(connection.secureSession().getRemoteId());

    }
    public void sendNewBlock(BlockWrapper blockWrapper) {
        log.debug("send a block hash is:+" + Hex.toHexString(blockWrapper.getBlock().getHashLow()));
        log.debug("ttl:" + blockWrapper.getTtl());
        this.messageQueue = new Libp2pMessageQueue(this);
        xdag.sendNewBlock(blockWrapper.getBlock(), blockWrapper.getTtl());
    }
    //获取对方的ip
    public String getIp(){
        return connection.remoteAddress().toString();
    }

    public void onDisconnect() {
        isDisconnected = true;
    }

    public boolean isDisconnected() {
        return isDisconnected;
    }

    public Node getNode(){
        return remotenode;
    }
    public Handler getHandler(){
        return handler;
    }
    public void setActive(boolean b) {
        isActive = b;
    }

    public boolean isActive() {
        return isActive;
    }
    public void dropConnection() {


    }
}
