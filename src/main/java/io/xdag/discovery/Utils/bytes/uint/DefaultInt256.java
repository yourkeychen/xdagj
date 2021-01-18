package io.xdag.discovery.Utils.bytes.uint;

import io.xdag.discovery.Utils.bytes.AbstractBytes32Backed;
import io.xdag.discovery.Utils.bytes.Bytes32;
import io.xdag.discovery.Utils.bytes.MutableBytes32;

import static com.google.common.base.Preconditions.checkArgument;

class DefaultInt256 extends AbstractBytes32Backed implements Int256 {

    DefaultInt256(final Bytes32 bytes) {
        super(bytes);
        checkArgument(
                bytes.size() == SIZE,
                "Invalid value for a UInt256: expecting %s bytes but got %s",
                SIZE,
                bytes.size());
    }

    // Note meant to be used directly, use Int256.MINUS_ONE instead
    static DefaultInt256 minusOne() {
        final MutableBytes32 v = MutableBytes32.create();
        v.fill((byte) 0xFF);
        return new DefaultInt256(v);
    }

    private Int256 binaryOp(final Int256 value, final UInt256Bytes.BinaryOp op) {
        final MutableBytes32 result = MutableBytes32.create();
        op.applyOp(getBytes(), value.getBytes(), result);
        return new DefaultInt256(result);
    }

    @Override
    public Int256 dividedBy(final Int256 value) {
        return binaryOp(value, Int256Bytes::divide);
    }

    @Override
    public Int256 mod(final Int256 value) {
        return binaryOp(value, Int256Bytes::mod);
    }

    @Override
    public int compareTo(final Int256 other) {
        final boolean thisNeg = this.isNegative();
        final boolean otherNeg = other.isNegative();

        if (thisNeg) {
            // We're negative, if the other isn't it is bigger, otherwise both negative => compare same as
            // unsigned.
            return otherNeg ? UInt256Bytes.compareUnsigned(getBytes(), other.getBytes()) : -1;
        }

        // We're positive, if the other isn't we are bigger, otherwise both are positive and we can use
        // unsigned comparison.
        return otherNeg ? 1 : UInt256Bytes.compareUnsigned(getBytes(), other.getBytes());
    }
}
