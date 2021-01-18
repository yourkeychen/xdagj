package io.xdag.discovery.Utils.bytes.uint;

import io.xdag.discovery.Utils.bytes.AbstractBytes32Backed;
import io.xdag.discovery.Utils.bytes.Bytes32;
import io.xdag.discovery.Utils.bytes.Bytes32s;

import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;

abstract class AbstractUInt256Value<T extends UInt256Value<T>> extends AbstractBytes32Backed
        implements UInt256Value<T> {

    private final Supplier<Counter<T>> mutableCtor;

    protected AbstractUInt256Value(final Bytes32 bytes, final Supplier<Counter<T>> mutableCtor) {
        super(bytes);
        this.mutableCtor = mutableCtor;
    }

    private T unaryOp(final UInt256Bytes.UnaryOp op) {
        final Counter<T> result = mutableCtor.get();
        op.applyOp(getBytes(), result.getBytes());
        return result.get();
    }

    protected T binaryOp(final UInt256Value<?> value, final UInt256Bytes.BinaryOp op) {
        final Counter<T> result = mutableCtor.get();
        op.applyOp(getBytes(), value.getBytes(), result.getBytes());
        return result.get();
    }

    private T binaryLongOp(final long value, final UInt256Bytes.BinaryLongOp op) {
        final Counter<T> result = mutableCtor.get();
        op.applyOp(getBytes(), value, result.getBytes());
        return result.get();
    }

    private T ternaryOp(
            final UInt256Value<?> v1, final UInt256Value<?> v2, final UInt256Bytes.TernaryOp op) {
        final Counter<T> result = mutableCtor.get();
        op.applyOp(getBytes(), v1.getBytes(), v2.getBytes(), result.getBytes());
        return result.get();
    }

    @Override
    public T copy() {
        final Counter<T> result = mutableCtor.get();
        getBytes().copyTo(result.getBytes());
        return result.get();
    }

    @Override
    public T plus(final T value) {
        return binaryOp(value, UInt256Bytes::add);
    }

    @Override
    public T plus(final long value) {
        checkArgument(value >= 0, "Invalid negative value %s", value);
        return binaryLongOp(value, UInt256Bytes::add);
    }

    @Override
    public T plusModulo(final T value, final UInt256 modulo) {
        return ternaryOp(value, modulo, UInt256Bytes::addModulo);
    }

    @Override
    public T minus(final T value) {
        return binaryOp(value, UInt256Bytes::subtract);
    }

    @Override
    public T minus(final long value) {
        checkArgument(value >= 0, "Invalid negative value %s", value);
        return binaryLongOp(value, UInt256Bytes::subtract);
    }

    @Override
    public T times(final T value) {
        return binaryOp(value, UInt256Bytes::multiply);
    }

    @Override
    public T times(final long value) {
        checkArgument(value >= 0, "Invalid negative value %s", value);
        return binaryLongOp(value, UInt256Bytes::multiply);
    }

    @Override
    public T timesModulo(final T value, final UInt256 modulo) {
        return ternaryOp(value, modulo, UInt256Bytes::multiplyModulo);
    }

    @Override
    public T dividedBy(final T value) {
        return binaryOp(value, UInt256Bytes::divide);
    }

    @Override
    public T dividedBy(final long value) {
        checkArgument(value >= 0, "Invalid negative value %s", value);
        return binaryLongOp(value, UInt256Bytes::divide);
    }

    @Override
    public T pow(final T value) {
        return binaryOp(value, UInt256Bytes::exponent);
    }

    @Override
    public T mod(final T value) {
        return binaryOp(value, UInt256Bytes::modulo);
    }

    @Override
    public T mod(final long value) {
        checkArgument(value >= 0, "Invalid negative value %s", value);
        return binaryLongOp(value, UInt256Bytes::modulo);
    }

    @Override
    public Int256 signExtent(final UInt256 value) {
        return new DefaultInt256(binaryOp(value, UInt256Bytes::signExtend).getBytes());
    }

    @Override
    public T and(final T value) {
        return binaryOp(value, Bytes32s::and);
    }

    @Override
    public T or(final T value) {
        return binaryOp(value, Bytes32s::or);
    }

    @Override
    public T xor(final T value) {
        return binaryOp(value, Bytes32s::xor);
    }

    @Override
    public T not() {
        return unaryOp(Bytes32s::not);
    }

    @Override
    public int compareTo(final T other) {
        return UInt256Bytes.compareUnsigned(getBytes(), other.getBytes());
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) return false;
        // Note that we do want strictly class equality in this case: we don't want 2 quantity of
        // mismatching unit to be considered equal, even if they do represent the same number.
        if (this.getClass() != other.getClass()) return false;

        final UInt256Value<?> that = (UInt256Value<?>) other;
        return this.getBytes().equals(that.getBytes());
    }

    @Override
    public int hashCode() {
        return bytes.hashCode();
    }

    @Override
    public String toString() {
        return UInt256Bytes.toString(getBytes());
    }
}
