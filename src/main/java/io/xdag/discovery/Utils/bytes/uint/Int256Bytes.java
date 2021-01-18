package io.xdag.discovery.Utils.bytes.uint;

import io.xdag.discovery.Utils.bytes.Bytes32;
import io.xdag.discovery.Utils.bytes.BytesValue;
import io.xdag.discovery.Utils.bytes.BytesValues;
import io.xdag.discovery.Utils.bytes.MutableBytes32;

import java.math.BigInteger;
import java.util.function.BinaryOperator;

abstract class Int256Bytes {

    private Int256Bytes() {}

    private static final byte ALL_ZERO_BYTE = (byte) 0x00;
    private static final byte ALL_ONE_BYTE = (byte) 0xFF;

    private static void copy(final BigInteger result, final MutableBytes32 destination) {
        final byte padding = result.signum() < 0 ? ALL_ONE_BYTE : ALL_ZERO_BYTE;
        UInt256Bytes.copyPadded(BytesValue.wrap(result.toByteArray()), destination, padding);
    }

    private static void doOnSignedBigInteger(
            final Bytes32 v1,
            final Bytes32 v2,
            final MutableBytes32 dest,
            final BinaryOperator<BigInteger> operator) {
        final BigInteger i1 = BytesValues.asSignedBigInteger(v1);
        final BigInteger i2 = BytesValues.asSignedBigInteger(v2);
        final BigInteger result = operator.apply(i1, i2);
        copy(result, dest);
    }

    // Tests if this value represents -2^255, that is the first byte is 1 followed by only 0. Used to
    // implement the overflow condition of the Yellow Paper in signedDivide().
    private static boolean isMinusP255(final Bytes32 v) {
        if (v.get(0) != (byte) 0x80) return false;

        byte b = 0;
        for (int i = 1; i < v.size(); i++) {
            b |= v.get(i);
        }
        return b == 0;
    }

    static void divide(final Bytes32 v1, final Bytes32 v2, final MutableBytes32 result) {
        if (v2.isZero()) {
            result.clear();
        } else if (v2.equals(Int256.MINUS_ONE.getBytes()) && isMinusP255(v2)) {
            // Set to -2^255.
            result.clear();
            result.set(0, (byte) 0x80);
        } else {
            doOnSignedBigInteger(v1, v2, result, BigInteger::divide);
        }
    }

    static void mod(final Bytes32 v1, final Bytes32 v2, final MutableBytes32 result) {
        if (v2.isZero()) {
            result.clear();
        } else {
            doOnSignedBigInteger(
                    v1,
                    v2,
                    result,
                    (val, mod) -> {
                        final BigInteger absModulo = val.abs().mod(mod.abs());
                        return val.signum() < 0 ? absModulo.negate() : absModulo;
                    });
        }
    }
}
