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

	IBlockWithProperty setSoundType(SoundType sound);
	IBlockWithProperty setLightOpacity(int opacity);
	IBlockWithProperty setLightLevel(float value);
	IBlockWithProperty setResistance(float resistance);
	IBlockWithProperty setHardness(float hardness);
	IBlockWithProperty setSlipperiness(float slipperiness);
	IBlockWithProperty setBeaconBase(boolean beaconBase);
	IBlockWithProperty setBoundingBox(AxisAlignedBB boundingBox);
	IBlockWithProperty setHarvestTool(String harvestTool);
	IBlockWithProperty setHarvestLevel(int harvestLevel);
	IBlockWithProperty setFull(boolean full);
	IBlockWithProperty setOpaque(boolean opaque);
	IBlockWithProperty setBlockLayer(BlockRenderLayer layer);
	IBlockWithProperty setFlammability(int flammability);
	IBlockWithProperty setFireSpreadSpeed(int fireSpreadSpeed);
	IBlockWithProperty setFireSource(boolean fireSource);
	IBlockWithProperty setFallable(boolean fallable);

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {
		ModelLoader.setCustomStateMapper((Block)this, new JAOPCAStateMap.Builder(getItemEntry().itemModelLocation).build());
	}
}
