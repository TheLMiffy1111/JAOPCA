package thelm.jaopca.modules;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import exter.foundry.fluid.FluidLiquidMetal;
import exter.foundry.fluid.LiquidMetalRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.block.BlockFluidBase;
import thelm.jaopca.api.fluid.FluidProperties;
import thelm.jaopca.api.fluid.IFluidWithProperty;
import thelm.jaopca.api.utils.Utils;

public class ModuleFoundry extends ModuleBase {

	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Copper", "Tin", "Bronze", "Electrum", "Invar", "Nickel", "Zinc", "Brass",
			"Silver", "Steel", "Cupronickel", "Lead", "Platinum", "Aluminium", "Alumina", "Chromium",
			"Signalum", "Lumium", "Enderium", "Osmium", "Manasteel", "Terrasteel", "ElvenElementium",
			"RedstoneAlloy", "EnergeticAlloy", "VibrantAlloy", "DarkSteel", "PulsatingIron",
			"ElectricalSteel", "Soularium"
			);

	public static final FluidProperties LIQUID_PROPERTIES = new FluidProperties().
			setDensityFunc(entry->2000).
			setTemperatureFunc(entry->MathHelper.clamp((int)(750*entry.getEnergyModifier())+750, 1500, 4000)).
			setLuminosityFunc(entry->15).
			setFillSound(SoundEvents.ITEM_BUCKET_FILL_LAVA).
			setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY_LAVA).
			setMaterial(Material.LAVA).
			setFluidClass(FluidLiquidMetalBase.class).
			setBlockFluidClass(BlockFluidLiquidMetalBase.class);

	public static final ItemEntry LIQUID_ENTRY = new ItemEntry(EnumEntryType.FLUID, "liquid", new ModelResourceLocation("jaopca:fluids/liquid#normal"), BLACKLIST).
			setProperties(LIQUID_PROPERTIES).
			setOreTypes(EnumOreType.INGOTS);

	@Override
	public String getName() {
		return "foundry";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(LIQUID_ENTRY);
	}

	//i hope this works
	@Override
	public void preInit() {
		Map<String, FluidLiquidMetal> registry;
		try {
			Field registryField = LiquidMetalRegistry.class.getDeclaredField("registry");
			registryField.setAccessible(true);
			registry = (Map<String, FluidLiquidMetal>)registryField.get(LiquidMetalRegistry.instance);
		}
		catch(NoSuchFieldException | SecurityException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("liquid")) {
			registry.put(entry.getOreName(), (FluidLiquidMetal)JAOPCAApi.FLUIDS_TABLE.get("liquid", entry.getOreName()));
		}
	}

	public static class FluidLiquidMetalBase extends FluidLiquidMetal implements IFluidWithProperty {

		public final IOreEntry oreEntry;
		public final ItemEntry itemEntry;

		public FluidLiquidMetalBase(ItemEntry itemEntry, IOreEntry oreEntry) {
			super(itemEntry.prefix+'_'+Utils.to_under_score(oreEntry.getOreName()), new ResourceLocation("jaopca:fluids/"+Utils.to_under_score(itemEntry.name)+"_still"), new ResourceLocation("jaopca:fluids/"+Utils.to_under_score(itemEntry.name)+"_flowing"), 0, false, 0, 0);
			this.setUnlocalizedName("jaopca."+itemEntry.name);
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
		public FluidLiquidMetalBase setLuminosity(int luminosity) {
			super.setLuminosity(luminosity);
			return this;
		}

		@Override
		public FluidLiquidMetalBase setDensity(int density) {
			super.setDensity(density);
			return this;
		}

		@Override
		public FluidLiquidMetalBase setTemperature(int temperature) {
			super.setTemperature(temperature);
			return this;
		}

		@Override
		public FluidLiquidMetalBase setViscosity(int viscosity) {
			super.setViscosity(viscosity);
			return this;
		}

		@Override
		public FluidLiquidMetalBase setGaseous(boolean isGaseous) {
			super.setGaseous(isGaseous);
			return this;
		}

		@Override
		public FluidLiquidMetalBase setRarity(EnumRarity rarity) {
			super.setRarity(rarity);
			return this;
		}

		@Override
		public FluidLiquidMetalBase setFillSound(SoundEvent fillSound) {
			super.setFillSound(fillSound);
			return this;
		}

		@Override
		public FluidLiquidMetalBase setEmptySound(SoundEvent emptySound) {
			super.setEmptySound(emptySound);
			return this;
		}

		protected int opacity = 255;

		@Override
		public FluidLiquidMetalBase setOpacity(int opacity) {
			this.opacity = opacity;
			return this;
		}

		@Override
		public int getColor() {
			return new Color(oreEntry.getColor()).brighter().brighter().getRGB()&0xFFFFFF | opacity<<24;
		}

		@Override
		public String getLocalizedName(FluidStack stack) {
			return Utils.smartLocalize(this.getUnlocalizedName(), this.getUnlocalizedName()+".%s", this.getOreEntry());
		}
	}

	public static class BlockFluidLiquidMetalBase extends BlockFluidBase {

		private IBlockState solidState;

		public BlockFluidLiquidMetalBase(IFluidWithProperty fluid, Material material) {
			super(fluid, material);
		}

		public void checkForHarden(World world, BlockPos pos, IBlockState state) {
			if(this.isSourceBlock(world, pos)) {
				if(this.solidState == null) {
					ItemStack item = Utils.getPreferredStack(Utils.getOres("block"+this.getOreEntry().getOreName()).stream().filter(stack->Block.getBlockFromItem(stack.getItem()) != Blocks.AIR).collect(Collectors.toList()));
					if(item != null) {
						this.solidState = this.getBlockStateFromItemStack(item);
					}
					else {
						return;
					}
				}
				if(this.tryToHarden(world, pos, pos.add(-1, 0, 0))) {
					return;
				}
				if(this.tryToHarden(world, pos, pos.add(1, 0, 0))) {
					return;
				}
				if(this.tryToHarden(world, pos, pos.add(0, -1, 0))) {
					return;
				}
				if(this.tryToHarden(world, pos, pos.add(0, 1, 0))) {
					return;
				}
				if(this.tryToHarden(world, pos, pos.add(0, 0, -1))) {
					return;
				}
				if(this.tryToHarden(world, pos, pos.add(0, 0, 1))) {
					return;
				}
			}
		}

		private IBlockState getBlockStateFromItemStack(ItemStack stack) {
			if(stack.getItem() instanceof ItemBlock) {
				Block block = ((ItemBlock)stack.getItem()).getBlock();
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
		public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
			return 300;
		}

		@Override
		public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
			return 0;
		}

		@Override
		public boolean displaceIfPossible(World world, BlockPos pos) {
			return !world.getBlockState(pos).getMaterial().isLiquid() && super.displaceIfPossible(world, pos);
		}

		@Override
		public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
			super.neighborChanged(state, world, pos, block);
			this.checkForHarden(world, pos, state);
		}

		@Override
		public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
			super.onBlockAdded(world, pos, state);
			this.checkForHarden(world, pos, state);
		}

		@Override
		public void onEntityCollidedWithBlock(World wWorld, BlockPos pos, IBlockState state, Entity entity) {
			if(entity instanceof EntityLivingBase) {
				entity.motionX *= 0.5;
				entity.motionZ *= 0.5;
			}
			if(!entity.isImmuneToFire()) {
				if(!(entity instanceof EntityItem)) {
					entity.attackEntityFrom(DamageSource.lava, 4F);
				}
				entity.setFire(15);
			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
			if(this.temperature < 1200) {
				return;
			}
			if(world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR && !world.getBlockState(pos.add(0, 1, 0)).isOpaqueCube()) {
				if(rand.nextInt(100) == 0) {
					double dx = pos.getX() + rand.nextFloat();
					double dy = pos.getY() + state.getBoundingBox((IBlockAccess)world, pos).maxY;
					double dz = pos.getZ() + rand.nextFloat();
					world.spawnParticle(EnumParticleTypes.LAVA, dx, dy, dz, 0.0, 0.0, 0.0, new int[0]);
					world.playSound(dx, dy, dz, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2f + rand.nextFloat() * 0.2f, 0.9f + rand.nextFloat() * 0.15f, false);
				}
				if(rand.nextInt(200) == 0) {
					world.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2f + rand.nextFloat() * 0.2f, 0.9f + rand.nextFloat() * 0.15f, false);
				}
			}
			BlockPos down = pos.down();
			if(rand.nextInt(10) == 0 && world.getBlockState(down).isSideSolid((IBlockAccess)world, down, EnumFacing.UP) && !world.getBlockState(pos.add(0, -1, 0)).getMaterial().blocksMovement()) {
				double dx = pos.getX() + rand.nextFloat();
				double dy = pos.getY() - 1.05;
				double dz = pos.getZ() + rand.nextFloat();
				world.spawnParticle(EnumParticleTypes.DRIP_LAVA, dx, dy, dz, 0.0, 0.0, 0.0, new int[0]);
			}
		}

		private boolean tryToHarden(World world, BlockPos pos, BlockPos npos) {
			if(world.getBlockState(npos).getMaterial() == Material.WATER) {
				world.setBlockState(pos, this.solidState);
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f, false);
				for(int i = 0; i < 8; ++i) {
					world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + Math.random(), pos.getY() + 1.2, pos.getZ() + Math.random(), 0.0, 0.0, 0.0, new int[0]);
				}
				return true;
			}
			return false;
		}
	}
}
