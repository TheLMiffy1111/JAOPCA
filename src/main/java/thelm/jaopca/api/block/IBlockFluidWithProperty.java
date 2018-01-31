package thelm.jaopca.api.block;

import net.minecraft.block.Block;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.utils.JAOPCAStateMap;

public interface IBlockFluidWithProperty extends IObjectWithProperty {

	default IBlockFluidWithProperty setQuantaPerBlock(int quantaPerBlock) {
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {
		ModelLoader.setCustomStateMapper((Block)this, new JAOPCAStateMap.Builder(getItemEntry().itemModelLocation).build());
	}
}
