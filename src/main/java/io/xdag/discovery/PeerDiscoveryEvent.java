package io.xdag.discovery;

import com.google.common.base.MoreObjects;
import io.xdag.discovery.peer.DiscoveryPeer;

public abstract class PeerDiscoveryEvent {
    private final DiscoveryPeer peer;
    private final long timestamp;

    private PeerDiscoveryEvent(final DiscoveryPeer peer, final long timestamp) {
        this.peer = peer;
        this.timestamp = timestamp;
    }

    public DiscoveryPeer getPeer() {
        return peer;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("peer", peer)
                .add("timestamp", timestamp)
                .toString();
    }

    /**
     * An event that is dispatched whenever we bond with a new peer. See Javadoc on <code>
     * PeerDiscoveryController</code> to understand when this happens.
     *
     * <p>{@link tech.pegasys.pantheon.ethereum.p2p.discovery.internal.PeerDiscoveryController}
     */
    public static class PeerBondedEvent extends PeerDiscoveryEvent {
        public PeerBondedEvent(final DiscoveryPeer peer, final long timestamp) {
            super(peer, timestamp);
        }
    }

    /**
     * An event that is dispatched whenever we drop a peer from the peer table. See Javadoc on <code>
     * PeerDiscoveryController</code> to understand when this happens.
     *
     * <p>{@link tech.pegasys.pantheon.ethereum.p2p.discovery.internal.PeerDiscoveryController}
     */
    public static class PeerDroppedEvent extends PeerDiscoveryEvent {
        public PeerDroppedEvent(final DiscoveryPeer peer, final long timestamp) {
            super(peer, timestamp);
        }
    }
}
