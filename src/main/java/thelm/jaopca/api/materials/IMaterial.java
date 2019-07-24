package thelm.jaopca.api.materials;

import java.util.Set;

import net.minecraft.item.Rarity;

public interface IMaterial extends Comparable<IMaterial> {

	String getName();

	MaterialType getType();

	Set<String> getAlternativeNames();

	IMaterial getExtra(int index);

	boolean hasExtra(int index);

	Set<String> getConfigModuleBlacklist();

	TextureType getTextureType();

	int getColor();

	boolean hasEffect();

	Rarity getDisplayRarity();

	@Override
	default int compareTo(IMaterial other) {
		return getName().compareTo(other.getName());
	}
}
