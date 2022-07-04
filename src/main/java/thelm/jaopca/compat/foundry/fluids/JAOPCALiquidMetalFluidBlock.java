package thelm.jaopca.compat.foundry.fluids;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.fluids.JAOPCAFluidBlock;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCALiquidMetalFluidBlock extends JAOPCAFluidBlock {

	private Optional<IBlockState> solidState = Optional.empty();

	public JAOPCALiquidMetalFluidBlock(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(fluid, settings);
	}

	public void tryHarden(World world, BlockPos pos, IBlockState state) {
		if(isSourceBlock(world, pos)) {
			if(!solidState.isPresent()) {
				IMiscHelper helper = MiscHelper.INSTANCE;
				List<ItemStack> list = OreDictionary.getOres(helper.getOredictName("block", getMaterial().getName()), false).
						stream().filter(is->ForgeRegistries.BLOCKS.getValue(is.getItem().getRegistryName()) != Blocks.AIR).
						collect(Collectors.toList());
				ItemStack stack = helper.getPreferredItemStack(list, 1);
				solidState = Optional.ofNullable(getBlockStateFromItemStack(stack));
			}
			if(!solidState.isPresent()) {
				return;
			}
			for(EnumFacing facing : EnumFacing.VALUES) {
				if(world.getBlockState(pos.offset(facing)).getMaterial() == Material.WATER) {
					int i;
					world.setBlockState(pos, solidState.get());
					world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f, false);
					for(i = 0; i < 8; i++) {
						world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + Math.random(), pos.getY() + 1.2D, pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
					}
					return;
				}
			}
		}
	}

	public IBlockState getBlockStateFromItemStack(ItemStack stack) {
		Block block = ForgeRegistries.BLOCKS.getValue(stack.getItem().getRegistryName());
		if(block != Blocks.AIR) {
			int meta = stack.getMetadata();
			for(IBlockState state : block.getBlockState().getValidStates()) {
				if(state != null && block.damageDropped(state) == meta) {
					return state;
				}
			}
		}
		return null;
	}

	@Override
	public boolean canDisplace(IBlockAccess world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		return !world.getBlockState(pos).getMaterial().isLiquid() && super.displaceIfPossible(world, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		super.neighborChanged(state, world, pos, block, fromPos);
		tryHarden(world, pos, state);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		tryHarden(world, pos, state);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if(entity instanceof EntityLivingBase) {
			entity.motionX *= 0.5;
			entity.motionZ *= 0.5;
		}
		if(!entity.isImmuneToFire()) {
			if(!(entity instanceof EntityItem)) {
				entity.attackEntityFrom(DamageSource.LAVA, 4);
			}
			entity.setFire(15);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(temperature < 1200) {
			return;
		}
		double dx;
		double dy;
		double dz;
		if(world.getBlockState(pos.up()).getMaterial() == Material.AIR && !world.getBlockState(pos.up()).isOpaqueCube()) {
			if(rand.nextInt(100) == 0) {
				dx = pos.getX()+rand.nextFloat();
				dy = pos.getY()+state.getBoundingBox(world, pos).maxY;
				dz = pos.getZ()+rand.nextFloat();
				world.spawnParticle(EnumParticleTypes.LAVA, dx, dy, dz, 0, 0, 0);
				world.playSound(dx, dy, dz, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F+rand.nextFloat() * 0.2F, 0.9F+rand.nextFloat()*0.15F, false);
			}
			if(rand.nextInt(200) == 0) {
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F+rand.nextFloat()*0.2F, 0.9F+rand.nextFloat()*0.15F, false);
			}
		}
		BlockPos down = pos.down();
		if(rand.nextInt(10) == 0 && world.getBlockState(down).isSideSolid(world, down, EnumFacing.UP) && !world.getBlockState(down).getMaterial().blocksMovement()) {
			dx = pos.getX()+rand.nextFloat();
			dy = pos.getY()-1.05D;
			dz = pos.getZ()+rand.nextFloat();
			world.spawnParticle(EnumParticleTypes.DRIP_LAVA, dx, dy, dz, 0, 0, 0);
		}
	}
}
