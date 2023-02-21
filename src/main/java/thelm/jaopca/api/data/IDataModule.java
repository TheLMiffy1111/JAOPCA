package thelm.jaopca.api.data;

public interface IDataModule extends Comparable<IDataModule> {

	String getName();

	void register();

	@Override
	default int compareTo(IDataModule other) {
		return getName().compareTo(other.getName());
	}
}
