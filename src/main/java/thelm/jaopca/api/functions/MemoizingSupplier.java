package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class MemoizingSupplier<T> implements Supplier<T> {

	private Supplier<T> delegate;
	private T value;

	private MemoizingSupplier(Supplier<T> delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	public static <T> MemoizingSupplier<T> of(Supplier<T> delegate) {
		return new MemoizingSupplier<>(delegate);
	}

	public static <V, T> MemoizingSupplier<T> of(Function<V, T> function, V value) {
		return new MemoizingSupplier<>(()->function.apply(value));
	}

	public static <V, T> MemoizingSupplier<T> of(Function<V, T> function, Supplier<V> value) {
		return new MemoizingSupplier<>(()->function.apply(value.get()));
	}

	@Override
	public T get() {
		if(delegate != null) {
			synchronized(this) {
				if(delegate != null) {
					value = delegate.get();
					delegate = null;
				}
			}
		}
		return value;
	}
}
