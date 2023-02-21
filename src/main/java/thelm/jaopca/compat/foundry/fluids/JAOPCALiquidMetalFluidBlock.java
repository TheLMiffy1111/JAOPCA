package thelm.jaopca.compat.foundry.fluids;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.fluids.JAOPCAFluidBlock;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCALiquidMetalFluidBlock extends JAOPCAFluidBlock {

	private Optional<Pair<Block, Integer>> solidState = Optional.empty();

	public JAOPCALiquidMetalFluidBlock(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		super(fluid, settings);
	}

	public void tryHarden(World world, int x, int y, int z) {
		if(isSourceBlock(world, x, y, z)) {
			if(!solidState.isPresent()) {
				IMiscHelper helper = MiscHelper.INSTANCE;
				List<ItemStack> list = OreDictionary.getOres(helper.getOredictName("block", getIMaterial().getName()), false).
						stream().filter(s->s.getItem() instanceof ItemBlock).
						collect(Collectors.toList());
				ItemStack stack = helper.getPreferredItemStack(list, 1);
				if(stack != null) {
					solidState = Optional.of(Pair.of(((ItemBlock)stack.getItem()).field_150939_a, stack.getItemDamage()));
				}
			}
			if(!solidState.isPresent()) {
				return;
			}
			for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
				if(world.getBlock(x+side.offsetX, y+side.offsetY, z+side.offsetZ).getMaterial() == Material.water) {
					world.setBlock(x, y, z, solidState.get().getLeft(), solidState.get().getRight(), 3);
					world.playSoundEffect(x+0.5, y+0.5, z+0.5, "random.fizz", 0.5F, 2+(world.rand.nextFloat()-world.rand.nextFloat())*0.8F);
					for(int i = 0; i < 8; i++) {
						world.spawnParticle("largesmoke", x+Math.random(), y+1.2, z+Math.random(), 0, 0, 0);
					}
					return;
				}
			}
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.displaceIfPossible(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		super.onNeighborBlockChange(world, x, y, z, neighbor);
		tryHarden(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		tryHarden(world, x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if(entity instanceof EntityLivingBase) {
			entity.motionX *= 0.5;
			entity.motionZ *= 0.5;
		}
		if(!entity.isImmuneToFire()) {
			if(!(entity instanceof EntityItem)) {
				entity.attackEntityFrom(DamageSource.lava, 4);
			}
			entity.setFire(15);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(temperature < 1200) {
			return;
		}
		double dx;
		double dy;
		double dz;
		if(world.getBlock(x, y+1, z).getMaterial() == Material.air && !world.getBlock(x, y+1, z).isOpaqueCube()) {
			if(rand.nextInt(100) == 0) {
				dx = x+rand.nextFloat();
				dy = y+maxY;
				dz = z+rand.nextFloat();
				world.spawnParticle("lava", dx, dy, dz, 0, 0, 0);
				world.playSound(dx, dy, dz, "liquid.lavapop", 0.2F+rand.nextFloat()*0.2F, 0.9F+rand.nextFloat()*0.15F, false);
			}
			if(rand.nextInt(200) == 0) {
				world.playSound(x, y, z, "liquid.lava", 0.2F+rand.nextFloat()*0.2F, 0.9F+rand.nextFloat()*0.15F, false);
			}
		}
		if(rand.nextInt(10) == 0 && World.doesBlockHaveSolidTopSurface(world, x, y-1, z) && !world.getBlock(x, y-2, z).getMaterial().blocksMovement()) {
			dx = x+rand.nextFloat();
			dy = y-1.05D;
			dz = z+rand.nextFloat();
			world.spawnParticle("dripLava", dx, dy, dz, 0, 0, 0);
		}
	}
}
