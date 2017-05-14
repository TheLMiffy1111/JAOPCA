package thelm.jaopca.api;

@FunctionalInterface
public interface ToFloatFunction<T> {
	
	float applyAsFloat(T value);
}
