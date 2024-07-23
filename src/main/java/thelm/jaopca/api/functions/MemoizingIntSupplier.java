package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class MemoizingIntSupplier implements IntSupplier {

	private IntSupplier delegate;
	private int value;

	private MemoizingIntSupplier(IntSupplier delegate) {
		this.delegate = Objects.requireNonNull(delegate);
	}

	public static MemoizingIntSupplier of(IntSupplier delegate) {
		return new MemoizingIntSupplier(delegate);
	}

	public static <T> MemoizingIntSupplier of(ToIntFunction<T> function, Supplier<T> value) {
		return new MemoizingIntSupplier(()->function.applyAsInt(value.get()));
	}

	@Override
	public int getAsInt() {
		if(delegate != null) {
			synchronized(this) {
				if(delegate != null) {
					value = delegate.getAsInt();
					delegate = null;
				}
			}
		}
		return value;
	}
}
