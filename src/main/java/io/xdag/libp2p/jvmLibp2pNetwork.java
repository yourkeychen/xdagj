package io.xdag.libp2p;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.libp2p.core.Host;
import io.libp2p.core.PeerId;
import io.libp2p.core.dsl.HostBuilder;
import io.libp2p.core.multiformats.Multiaddr;
import io.libp2p.discovery.MDnsDiscovery;
import io.xdag.libp2p.Handler.Handler;
import io.xdag.libp2p.Utils.IpUtil;
import io.xdag.libp2p.manager.Libp2pPeerManager;
import kotlin.Unit;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class jvmLibp2pNetwork {
    Host host;
    Node node = new Node();
    private final InetAddress privateAddress = IpUtil.getLocalAddress();
    private Set<Node> peers = new HashSet<>();
    private Map<PeerId, Node> knownNodes = Collections.synchronizedMap(new HashMap<>());
    private MDnsDiscovery peerFinder;
    private final String serviceTag = "_ipfs-discovery._udp";
    private final String serviceTagLocal = serviceTag+".local.";
    private int queryInterval = 6000;
    private final ScheduledExecutorService scheduler;
    Libp2pPeerManager peerManager;
    Handler handler;
    //todo:随机数端口改成kernel定义
    Random random=new Random();
    int port=random.nextInt(55536)+10000;

    public jvmLibp2pNetwork() {

        InetAddress privateAddress = IpUtil.getLocalAddress();
        scheduler = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("libp2p-%d").build());
        peerManager = new Libp2pPeerManager(scheduler);

        handler = new Handler();

        host = new HostBuilder().protocol(handler).listen("/ip4/" + privateAddress.getHostAddress() + "/tcp/" + port).build();


        host.addConnectionHandler(peerManager);

    }
    //把发现网络抽离
    public void start(){
        try {
            host.start().get();
            log.info("Node started and listening on ");
            log.info(host.listenAddresses().toString());
            peerFinder = new MDnsDiscovery(host,serviceTagLocal,queryInterval,privateAddress);
            peerFinder.getNewPeerFoundListeners().add(peerInfo -> {
                System.out.println("find peer : " + peerInfo.getPeerId().toString());
                Unit u = Unit.INSTANCE;

                if (!peerInfo.getAddresses().toString().contains(this.getAddress()) && !knownNodes.containsKey(peerInfo.getPeerId())) {


                    node.setPeerInfo(peerInfo);

                    knownNodes.put(peerInfo.getPeerId(), node);
                    peers.add(node);


                    String ip = peerInfo.getAddresses().toString() + "/ipfs/" +
                            peerInfo.getPeerId().toString();
                    ip = ip.replace("[", "").replace("]", "");
                    System.out.println(ip);
                    Multiaddr address = Multiaddr.fromString(ip);
                    handler.dial(this.host, address);

                }
                return u;
            });
            peerFinder.start();
            log.info("Peer finder started ");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        host.stop();
        peerFinder.stop();
        scheduler.shutdownNow();
    }


    public Host getNode(){
        return host;
    }

    public String getAddress(){
        return "/ip4/"+privateAddress.getHostAddress()+
                "/tcp/"+port;
    }

}

