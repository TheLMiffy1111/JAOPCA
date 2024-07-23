package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MemoizingBooleanSupplier implements BooleanSupplier {

	private BooleanSupplier delegate;
	private boolean value;

	private MemoizingBooleanSupplier(BooleanSupplier delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	public static MemoizingBooleanSupplier of(BooleanSupplier delegate) {
		return new MemoizingBooleanSupplier(delegate);
	}

	public static <T> MemoizingBooleanSupplier of(Predicate<T> function, Supplier<T> value) {
		return new MemoizingBooleanSupplier(()->function.test(value.get()));
	}

	@Override
	public boolean getAsBoolean() {
		if(delegate != null) {
			synchronized(this) {
				if(delegate != null) {
					value = delegate.getAsBoolean();
					delegate = null;
				}
			}
		}
		return value;
	}
}
