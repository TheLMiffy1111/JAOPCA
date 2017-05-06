package thelm.jaopca.api.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public class BlockBase extends Block {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public BlockBase(Material material, MapColor mapColor, ItemEntry itemEntry, IOreEntry oreEntry) {
		super(material, mapColor);
		setUnlocalizedName("jaopca."+itemEntry.name);
		setRegistryName("jaopca:block_"+itemEntry.name+oreEntry.getOreName());
		this.oreEntry = oreEntry;
		this.itemEntry = itemEntry;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockBase setSoundType(SoundType sound) {
		super.setSoundType(sound);
		return this;
	}

	@Override
	public BlockBase setLightOpacity(int opacity) {
		super.setLightOpacity(opacity);
		return this;
	}

	@Override
	public BlockBase setLightLevel(float value) {
		super.setLightLevel(value);
		return this;
	}

	@Override
	public BlockBase setResistance(float resistance) {
		super.setResistance(resistance);
		return this;
	}

	@Override
	public BlockBase setHardness(float hardness) {
		super.setHardness(hardness);
		return this;
	}

	public BlockBase setHarvestLvl(String toolClass, int level) {
		super.setHarvestLevel(toolClass, level);
		return this;
	}

	public BlockBase setSlipperiness(float slipperiness) {
		this.slipperiness = slipperiness;
		return this;
	}

	/*======================================== FALLING =====================================*/

	protected boolean fallable = false;

	public int tickRate(World worldIn) {
		return 2;
	}

	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if(fallable) {
			worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
		}
	}

	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if(fallable) {
			worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
		}
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(fallable && !worldIn.isRemote) {
			checkFallable(worldIn, pos);
		}
	}

	private void checkFallable(World worldIn, BlockPos pos) {
		if(fallable && (worldIn.isAirBlock(pos.down()) || BlockFalling.canFallThrough(worldIn.getBlockState(pos.down()))) && pos.getY() >= 0) {
			int i = 32;

			if(!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
				if(!worldIn.isRemote) {
					EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
					worldIn.spawnEntity(entityfallingblock);
				}
			}
			else {
				IBlockState state = worldIn.getBlockState(pos);
				worldIn.setBlockToAir(pos);
				BlockPos blockpos;

				for(blockpos = pos.down(); (worldIn.isAirBlock(blockpos) || BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {}

				if(blockpos.getY() > 0) {
					worldIn.setBlockState(blockpos.up(), state);
				}
			}
		}
	}

	public BlockBase setFallable() {
		fallable = true;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(fallable && rand.nextInt(16) == 0) {
			BlockPos blockpos = pos.down();

			if(BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) {
				double d0 = (double)((float)pos.getX() + rand.nextFloat());
				double d1 = (double)pos.getY() - 0.05D;
				double d2 = (double)((float)pos.getZ() + rand.nextFloat());
				worldIn.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[] {Block.getStateId(stateIn)});
			}
		}
	}
}
