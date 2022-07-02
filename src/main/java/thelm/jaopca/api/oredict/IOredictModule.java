package thelm.jaopca.api.oredict;

public interface IOredictModule extends Comparable<IOredictModule> {

	String getName();

	void register();

	@Override
	default int compareTo(IOredictModule other) {
		return getName().compareTo(other.getName());
	}
}
