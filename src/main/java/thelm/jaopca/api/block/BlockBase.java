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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public class BlockBase extends Block implements IBlockWithProperty {

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
	public IOreEntry getOreEntry() {
		return oreEntry;
	}

	@Override
	public ItemEntry getItemEntry() {
		return itemEntry;
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

	@Override
	public BlockBase setSlipperiness(float slipperiness) {
		this.slipperiness = slipperiness;
		return this;
	}

	/*======================================== BEACON ======================================*/

	protected boolean beaconBase = false;

	@Override
	public BlockBase setBeaconBase(boolean beaconBase) {
		this.beaconBase = beaconBase;
		return this;
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
		return beaconBase;
	}

	/*========================================= AABB =======================================*/

	protected AxisAlignedBB boundingBox = FULL_BLOCK_AABB;

	@Override
	public BlockBase setBoundingBox(AxisAlignedBB boundingBox) {
		this.boundingBox = boundingBox;
		return this;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return boundingBox;
	}

	/*======================================== HARVEST =====================================*/

	protected String harvestTool = null;
	protected int harvestLevel = -1;

	@Override
	public BlockBase setHarvestTool(String harvestTool) {
		this.harvestTool = harvestTool;
		return this;
	}

	@Override
	public BlockBase setHarvestLevel(int harvestLevel) {
		this.harvestLevel = harvestLevel;
		return this;
	}

	@Override
	public String getHarvestTool(IBlockState state) {
		return harvestTool;
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return harvestLevel;
	}

	/*======================================== OPACITY =====================================*/

	protected boolean full = true;
	protected boolean opaque = true;

	@Override
	public BlockBase setFull(boolean full) {
		this.full = full;
		return this;
	}

	@Override
	public BlockBase setOpaque(boolean opaque) {
		this.opaque = opaque;
		return this;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return full;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return opaque;
	}

	/*======================================== RENDER ======================================*/

	protected BlockRenderLayer layer = BlockRenderLayer.CUTOUT;

	@Override
	public BlockBase setBlockLayer(BlockRenderLayer layer) {
		this.layer = layer;
		return this;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return layer;
	}

	/*========================================= FIRE =======================================*/

	protected int flammability = 0;
	protected int fireSpreadSpeed = 0;
	protected boolean fireSource = false;

	@Override
	public BlockBase setFlammability(int flammability) {
		this.flammability = flammability;
		return this;
	}

	@Override
	public BlockBase setFireSpreadSpeed(int fireSpreadSpeed) {
		this.fireSpreadSpeed = fireSpreadSpeed;
		return this;
	}

	@Override
	public BlockBase setFireSource(boolean fireSource) {
		this.fireSource = fireSource;
		return this;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return flammability;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return fireSpreadSpeed;
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		return fireSource && side == EnumFacing.UP;
	}

	/*======================================== FALLING =====================================*/

	protected boolean fallable = false;

	@Override
	public int tickRate(World worldIn) {
		return 2;
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if(fallable) {
			worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if(fallable) {
			worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
		}
	}

	@Override
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
					EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, worldIn.getBlockState(pos));
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

	@Override
	public BlockBase setFallable(boolean fallable) {
		this.fallable = fallable;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(fallable && rand.nextInt(16) == 0) {
			BlockPos blockpos = pos.down();

			if(BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) {
				double d0 = pos.getX() + rand.nextFloat();
				double d1 = pos.getY() - 0.05D;
				double d2 = pos.getZ() + rand.nextFloat();
				worldIn.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[] {Block.getStateId(stateIn)});
			}
		}
	}

	/*======================================== UNUSED ======================================*/

	@Override
	public boolean hasSubtypes() {
		return false;
	}
}
