package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

public class MemoizingDoubleSupplier implements DoubleSupplier {

	private DoubleSupplier delegate;
	private double value;

	private MemoizingDoubleSupplier(DoubleSupplier delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	public static MemoizingDoubleSupplier of(DoubleSupplier delegate) {
		return new MemoizingDoubleSupplier(delegate);
	}

	public static <T> MemoizingDoubleSupplier of(ToDoubleFunction<T> function, T value) {
		return new MemoizingDoubleSupplier(()->function.applyAsDouble(value));
	}

	public static <T> MemoizingDoubleSupplier of(ToDoubleFunction<T> function, Supplier<T> value) {
		return new MemoizingDoubleSupplier(()->function.applyAsDouble(value.get()));
	}

	@Override
	public double getAsDouble() {
		if(delegate != null) {
			synchronized(this) {
				if(delegate != null) {
					value = delegate.getAsDouble();
					delegate = null;
				}
			}
		}
		return value;
	}
}
