package thelm.jaopca.api.functions;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class MemoizingSuppliers {

	public static MemoizingBooleanSupplier of(BooleanSupplier delegate) {
		return MemoizingBooleanSupplier.of(delegate);
	}

	public static <T> MemoizingBooleanSupplier of(Predicate<T> function, T value) {
		return MemoizingBooleanSupplier.of(function, value);
	}

	public static <T> MemoizingBooleanSupplier of(Predicate<T> function, Supplier<T> value) {
		return MemoizingBooleanSupplier.of(function, value);
	}

	public static MemoizingIntSupplier of(IntSupplier delegate) {
		return MemoizingIntSupplier.of(delegate);
	}

	public static <T> MemoizingIntSupplier of(ToIntFunction<T> function, T value) {
		return MemoizingIntSupplier.of(function, value);
	}

	public static <T> MemoizingIntSupplier of(ToIntFunction<T> function, Supplier<T> value) {
		return MemoizingIntSupplier.of(function, value);
	}

	public static MemoizingDoubleSupplier of(DoubleSupplier delegate) {
		return MemoizingDoubleSupplier.of(delegate);
	}

	public static <T> MemoizingDoubleSupplier of(ToDoubleFunction<T> function, T value) {
		return MemoizingDoubleSupplier.of(function, value);
	}

	public static <T> MemoizingDoubleSupplier of(ToDoubleFunction<T> function, Supplier<T> value) {
		return MemoizingDoubleSupplier.of(function, value);
	}

	public static MemoizingLongSupplier of(LongSupplier delegate) {
		return MemoizingLongSupplier.of(delegate);
	}

	public static <T> MemoizingLongSupplier of(ToLongFunction<T> function, T value) {
		return MemoizingLongSupplier.of(function, value);
	}

	public static <T> MemoizingLongSupplier of(ToLongFunction<T> function, Supplier<T> value) {
		return MemoizingLongSupplier.of(function, value);
	}

	public static <T> MemoizingSupplier<T> of(Supplier<T> delegate) {
		return MemoizingSupplier.of(delegate);
	}

	public static <V, T> MemoizingSupplier<T> of(Function<V, T> function, V value) {
		return MemoizingSupplier.of(function, value);
	}

	public static <V, T> MemoizingSupplier<T> of(Function<V, T> function, Supplier<V> value) {
		return MemoizingSupplier.of(function, value);
	}
}
