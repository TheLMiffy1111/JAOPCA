package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;

public class MemoizingLongSupplier implements LongSupplier {

	private LongSupplier delegate;
	private long value;

	private MemoizingLongSupplier(LongSupplier delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	public static MemoizingLongSupplier of(LongSupplier delegate) {
		return new MemoizingLongSupplier(delegate);
	}

	public static <T> MemoizingLongSupplier of(ToLongFunction<T> function, T value) {
		return new MemoizingLongSupplier(()->function.applyAsLong(value));
	}

	public static <T> MemoizingLongSupplier of(ToLongFunction<T> function, Supplier<T> value) {
		return new MemoizingLongSupplier(()->function.applyAsLong(value.get()));
	}

	@Override
	public long getAsLong() {
		if(delegate != null) {
			synchronized(this) {
				if(delegate != null) {
					value = delegate.getAsLong();
					delegate = null;
				}
			}
		}
		return value;
	}
}
