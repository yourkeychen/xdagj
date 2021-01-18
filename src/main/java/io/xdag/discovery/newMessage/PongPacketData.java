package io.xdag.discovery.newMessage;

import io.xdag.discovery.Utils.PacketData;
import io.xdag.discovery.Utils.RLPInput;
import io.xdag.discovery.Utils.bytes.BytesValue;
import io.xdag.discovery.Utils.bytes.RLPOutput;
import io.xdag.discovery.peer.Endpoint;

public class PongPacketData implements PacketData {
    private final Endpoint to;

    /* Hash of the PING packet. */
    private final BytesValue pingHash;

    /* In millis after epoch. */
    private final long expiration;

    private PongPacketData(final Endpoint to, final BytesValue pingHash, final long expiration) {
        this.to = to;
        this.pingHash = pingHash;
        this.expiration = expiration;
    }

    public static PongPacketData create(final Endpoint to, final BytesValue pingHash) {
        return new PongPacketData(
                to, pingHash, System.currentTimeMillis() + PacketData.DEFAULT_EXPIRATION_PERIOD_MS);
    }

    public static PongPacketData readFrom(final RLPInput in) {
        in.enterList();
        final Endpoint to = Endpoint.decodeStandalone(in);
        final BytesValue hash = in.readBytesValue();
        final long expiration = in.readLongScalar();
        in.leaveList();
        return new PongPacketData(to, hash, expiration);
    }

    @Override
    public void writeTo(final RLPOutput out) {
        out.startList();
        to.encodeStandalone(out);
        out.writeBytesValue(pingHash);
        out.writeLongScalar(expiration);
        out.endList();
    }

    @Override
    public String toString() {
        return "PongPacketData{"
                + "to="
                + to
                + ", pingHash="
                + pingHash
                + ", expiration="
                + expiration
                + '}';
    }

    public Endpoint getTo() {
        return to;
    }

    public BytesValue getPingHash() {
        return pingHash;
    }

    public long getExpiration() {
        return expiration;
    }

}
