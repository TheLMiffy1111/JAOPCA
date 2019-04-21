package thelm.jaopca.api.materials;

import java.util.Set;

import net.minecraft.item.EnumRarity;

public interface IMaterial extends Comparable<IMaterial> {

	String getName();

	EnumMaterialType getType();

	IMaterial getExtra(int index);

	boolean hasExtra(int index);

	Set<String> getConfigModuleBlacklist();

	int getColor();

	boolean hasEffect();

	EnumRarity getDisplayRarity();

	@Override
	default int compareTo(IMaterial other) {
		return getName().compareTo(other.getName());
	}
}
