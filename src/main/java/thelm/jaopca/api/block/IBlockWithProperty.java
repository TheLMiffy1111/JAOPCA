package thelm.jaopca.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.utils.JAOPCAStateMap;

public interface IBlockWithProperty extends IObjectWithProperty {

	default IBlockWithProperty setSoundType(SoundType sound) {
		return this;
	}

	default IBlockWithProperty setLightOpacity(int opacity) {
		return this;
	}

	default IBlockWithProperty setLightLevel(float value) {
		return this;
	}

	default IBlockWithProperty setResistance(float resistance) {
		return this;
	}

	default IBlockWithProperty setHardness(float hardness) {
		return this;
	}

	default IBlockWithProperty setSlipperiness(float slipperiness) {
		return this;
	}

	default IBlockWithProperty setBeaconBase(boolean beaconBase) {
		return this;
	}

	default IBlockWithProperty setBoundingBox(AxisAlignedBB boundingBox) {
		return this;
	}

	default IBlockWithProperty setHarvestTool(String harvestTool) {
		return this;
	}

	default IBlockWithProperty setHarvestLevel(int harvestLevel) {
		return this;
	}

	default IBlockWithProperty setFull(boolean full) {
		return this;
	}

	default IBlockWithProperty setOpaque(boolean opaque) {
		return this;
	}

	default IBlockWithProperty setBlockLayer(BlockRenderLayer layer) {
		return this;
	}

	default IBlockWithProperty setFlammability(int flammability) {
		return this;
	}

	default IBlockWithProperty setFireSpreadSpeed(int fireSpreadSpeed) {
		return this;
	}

	default IBlockWithProperty setFireSource(boolean fireSource) {
		return this;
	}

	default IBlockWithProperty setFallable(boolean fallable) {
		return this;
	}

	default boolean hasSubtypes() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {
		ModelLoader.setCustomStateMapper((Block)this, new JAOPCAStateMap.Builder(getItemEntry().itemModelLocation).build());
	}
}
