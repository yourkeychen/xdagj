package io.xdag.discovery.Utils.bytes.uint;

import io.xdag.discovery.Utils.bytes.Bytes32;

import java.math.BigInteger;

public interface UInt256 extends UInt256Value<UInt256> {
    /** The value 0. */
    UInt256 ZERO = of(0);
    /** The value 1. */
    UInt256 ONE = of(1);
    /** The value 32. */
    UInt256 U_32 = of(32);

    static UInt256 of(final long value) {
        return new DefaultUInt256(UInt256Bytes.of(value));
    }

    static UInt256 of(final BigInteger value) {
        return new DefaultUInt256(UInt256Bytes.of(value));
    }

    static UInt256 wrap(final Bytes32 value) {
        return new DefaultUInt256(value);
    }

    static Counter<UInt256> newCounter() {
        return DefaultUInt256.newVar();
    }

    static Counter<UInt256> newCounter(final UInt256Value<?> initialValue) {
        final Counter<UInt256> c = DefaultUInt256.newVar();
        initialValue.getBytes().copyTo(c.getBytes());
        return c;
    }

    static UInt256 fromHexString(final String str) {
        return new DefaultUInt256(Bytes32.fromHexStringLenient(str));
    }
}
