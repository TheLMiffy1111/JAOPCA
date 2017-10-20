package thelm.jaopca.api;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IObjectWithProperty {

	IOreEntry getOreEntry();
	ItemEntry getItemEntry();

	default int getMaxMeta() {
		return 0;
	}
	default boolean hasMeta(int meta) {
		return meta <= getMaxMeta();
	}
	default String getPrefix(int meta) {
		return getItemEntry().prefix;
	}

	@SideOnly(Side.CLIENT)
	void registerModels();
}
