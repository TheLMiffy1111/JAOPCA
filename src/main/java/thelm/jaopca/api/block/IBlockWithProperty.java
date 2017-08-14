package thelm.jaopca.api.block;

import net.minecraft.block.SoundType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

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
}
