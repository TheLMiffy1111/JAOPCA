package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.bartz24.skyresources.alchemy.block.CondenserBlock;
import com.bartz24.skyresources.alchemy.crucible.CrucibleRecipes;
import com.bartz24.skyresources.alchemy.fluid.FluidCrystalBlock;
import com.bartz24.skyresources.alchemy.fluid.FluidMoltenCrystalBlock;
import com.bartz24.skyresources.alchemy.fluid.FluidRegisterInfo;
import com.bartz24.skyresources.config.ConfigOptions;
import com.bartz24.skyresources.registry.ModBlocks;
import com.bartz24.skyresources.registry.ModFluids;
import com.bartz24.skyresources.registry.ModItems;
import com.bartz24.skyresources.technology.cauldron.CauldronCleanRecipes;
import com.bartz24.skyresources.technology.concentrator.ConcentratorRecipes;
import com.bartz24.skyresources.technology.rockgrinder.RockGrinderRecipes;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.block.IBlockFluidWithProperty;
import thelm.jaopca.api.fluid.FluidProperties;
import thelm.jaopca.api.fluid.IFluidWithProperty;
import thelm.jaopca.api.utils.Utils;

public class ModuleSkyResources extends ModuleBase {

	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Copper", "Tin", "Silver", "Zinc", "Nickel", "Platinum", "Aluminum", "Lead", "Mercury", "Quartz", "Cobalt", "Ardite",
			"Adamantine", "Coldiron", "Osmium", "Lapis", "Draconium", "Certus"
			);
	public static final ArrayList<String> GEM_BLACKLIST = Lists.<String>newArrayList(
			"Emerald", "Ruby", "Sapphire", "Peridot", "RedGarnet", "YellowGarnet", "Apatite", "Amber", "Lepidolite", "Malachite", "Onyx", "Moldavite",
			"Agate", "Opal", "Amethyst", "Jasper", "Aquamarine", "Heliodor", "Turquoise", "Moonstone", "Morganite", "Carnelian", "Beryl", "GoldenBeryl",
			"Citrine", "Indicolite", "Garnet", "Topaz", "Ametrine", "Tanzanite", "VioletSappphire", "Alexandrite", "BlueTopaz", "Spinel", "Iolite",
			"BlackDiamond", "Chaos", "EnderEssence", "Quartz", "Lapis", "QuartzBlack", "CertusQuartz"
			);

	public static final HashMap<String,Integer> ORE_RARITYS = Maps.<String,Integer>newHashMap();
	public static final HashMap<String,Float> ORE_GEM_RARITYS = Maps.<String,Float>newHashMap();

	public static final FluidProperties DIRTY_CRYSTAL_FLUID_PROPERTIES = new FluidProperties().
			setBlockFluidClass(BlockDirtyCrystalFluidBase.class);
	public static final FluidProperties CRYSTAL_FLUID_PROPERTIES = new FluidProperties().
			setBlockFluidClass(BlockCrystalFluidBase.class);
	public static final FluidProperties MOLTEN_CRYSTAL_FLUID_PROPERTIES = new FluidProperties().
			setMaterial(Material.LAVA).
			setBlockFluidClass(BlockMoltenCrystalFluidBase.class);

	public static final ItemEntry CRYSTAL_SHARD_ENTRY = new ItemEntry(EnumEntryType.ITEM, "shardCrystal", "shardCrystal", new ModelResourceLocation("jaopca:shard_crystal#inventory"), BLACKLIST);
	public static final ItemEntry DIRTY_CRYSTAL_FLUID_ENTRY = new ItemEntry(EnumEntryType.FLUID, "dirtyCrystalFluid", new ModelResourceLocation("jaopca:fluids/dirty_crystal_fluid#normal"), BLACKLIST).setFluidProperties(DIRTY_CRYSTAL_FLUID_PROPERTIES);
	public static final ItemEntry CRYSTAL_FLUID_ENTRY = new ItemEntry(EnumEntryType.FLUID, "crystalFluid", new ModelResourceLocation("jaopca:fluids/crystal_fluid#normal"), BLACKLIST).setFluidProperties(CRYSTAL_FLUID_PROPERTIES);
	public static final ItemEntry MOLTEN_CRYSTAL_FLUID_ENTRY = new ItemEntry(EnumEntryType.FLUID, "moltenCrystalFluid", new ModelResourceLocation("jaopca:fluids/molten_crystal_fluid#normal"), BLACKLIST).setFluidProperties(MOLTEN_CRYSTAL_FLUID_PROPERTIES);
	public static final ItemEntry DIRTY_GEM_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dirtyGem", new ModelResourceLocation("jaopca:dirty_gem#inventory"), GEM_BLACKLIST).
			setOreTypes(EnumOreType.GEM);

	@Override
	public String getName() {
		return "skyresources";
	}

	@Override
	public List<String> getOreBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void registerConfigsPre(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.ORE_TYPE_TO_ORES_MAP.get(EnumOreType.INGOT)) {
			if(!BLACKLIST.contains(entry.getOreName())) {
				if(config.get(Utils.to_under_score(entry.getOreName()), "skyResourcesIsMolten", false).setRequiresMcRestart(true).getBoolean()) {
					DIRTY_CRYSTAL_FLUID_ENTRY.blacklist.add(entry.getOreName());
					CRYSTAL_FLUID_ENTRY.blacklist.add(entry.getOreName());
				}
				else {
					MOLTEN_CRYSTAL_FLUID_ENTRY.blacklist.add(entry.getOreName());
				}
			}
		}
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(CRYSTAL_SHARD_ENTRY, DIRTY_CRYSTAL_FLUID_ENTRY, CRYSTAL_FLUID_ENTRY, MOLTEN_CRYSTAL_FLUID_ENTRY, DIRTY_GEM_ENTRY);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dirtyGem")) {
			ORE_GEM_RARITYS.put(entry.getOreName(), (float)config.get(Utils.to_under_score(entry.getOreName()), "skyResourcesRarity", 0.006D).setRequiresMcRestart(true).getDouble());
		}
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			ORE_RARITYS.put(entry.getOreName(), config.get(Utils.to_under_score(entry.getOreName()), "skyResourcesRarity", 6).setRequiresMcRestart(true).getInt());
		}
	}

	//WHY
	@Override
	public void postInit() {
		boolean easy = ConfigOptions.easyMode;
		//Many things are hardcoded and has no custom recipes
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dirtyCrystalFluid")) {
			int rarity = ORE_RARITYS.get(entry.getOreName());
			Fluid dcfluid = JAOPCAApi.FLUIDS_TABLE.get("dirtyCrystalFluid", entry.getOreName());
			ModFluids.addCrystalFluid(StringUtils.uncapitalize(entry.getOreName()), -1, rarity, FluidRegisterInfo.CrystalFluidType.NORMAL);
			ModFluids.dirtyCrystalFluids.add(dcfluid);
			ModBlocks.dirtyCrystalFluidBlocks.add(dcfluid.getBlock());
			Fluid cfluid = JAOPCAApi.FLUIDS_TABLE.get("crystalFluid", entry.getOreName());
			ModFluids.crystalFluids.add(cfluid);
			ModBlocks.crystalFluidBlocks.add(cfluid.getBlock());

			CrucibleRecipes.addRecipe(new FluidStack(dcfluid, 1000), Utils.getOreStack("shardCrystal", entry, 1));
			ItemStack oreStack = Utils.getOreStack("ore", entry, 1);
			if(oreStack.getItem() instanceof ItemBlock) {
				ConcentratorRecipes.addRecipe(Block.getBlockFromItem(oreStack.getItem()).getStateFromMeta(oreStack.getMetadata()), rarity*(easy?50:100), Utils.getJAOPCAOrOreStack("crystalShardSky", "crystalShard", entry, ConfigOptions.crystalConcentratorAmount), ModBlocks.compressedStone.getDefaultState());
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("moltenCrystalFluid")) {
			int rarity = ORE_RARITYS.get(entry.getOreName());
			Fluid mcfluid = JAOPCAApi.FLUIDS_TABLE.get("moltenCrystalFluid", entry.getOreName());
			ModFluids.addCrystalFluid(StringUtils.uncapitalize(entry.getOreName()), -1, rarity, FluidRegisterInfo.CrystalFluidType.MOLTEN);
			ModFluids.moltenCrystalFluids.add(mcfluid);
			ModBlocks.moltenCrystalFluidBlocks.add(mcfluid.getBlock());

			CrucibleRecipes.addRecipe(new FluidStack(mcfluid, 1000), Utils.getOreStack("shardCrystal", entry, 1));
			ItemStack oreStack = Utils.getOreStack("ore", entry, 1);
			if(oreStack.getItem() instanceof ItemBlock) {
				ConcentratorRecipes.addRecipe(Block.getBlockFromItem(oreStack.getItem()).getStateFromMeta(oreStack.getMetadata()), rarity*(easy?50:100), Utils.getJAOPCAOrOreStack("crystalShardSky", "crystalShard", entry, ConfigOptions.crystalConcentratorAmount), ModBlocks.compressedNetherrack.getDefaultState());
			}
		}

		if(easy) {
			for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("shardCrystal")) {
				Utils.addSmelting(Utils.getOreStack("shardCrystal", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.1F);
			}
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dirtyGem")) {
			float rarity = ORE_GEM_RARITYS.get(entry.getOreName());
			RockGrinderRecipes.addRecipe(Utils.getOreStack("dirtyGem", entry, 1), false, Blocks.STONE.getDefaultState(), rarity*(easy?1.5F:1F));
			//same thing right
			CauldronCleanRecipes.addRecipe(Utils.getOreStack("gem", entry, 1), 1F, Utils.getOreStack("dirtyGem", entry, 1));
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			int rarity = ORE_RARITYS.get(entry.getOreName());
			ItemStack dust = Utils.getOreStack("dust", entry, 1);

			if(dust != null) {
				CauldronCleanRecipes.addRecipe(dust, 1F/(rarity*(easy?7F:9F)), new ItemStack(ModItems.techComponent, 1, 0));
			}
		}
	}

	public static class BlockCrystalFluidBase extends FluidCrystalBlock implements IBlockFluidWithProperty {

		public final IOreEntry oreEntry;
		public final ItemEntry itemEntry;

		public BlockCrystalFluidBase(IFluidWithProperty fluid, Material material) {
			super((Fluid)fluid, material, "", "jaopca:fluid_"+fluid.getItemEntry().name+fluid.getOreEntry().getOreName());
			oreEntry = fluid.getOreEntry();
			itemEntry = fluid.getItemEntry();
			setUnlocalizedName("jaopca."+itemEntry.name);
		}

		@Override
		public IOreEntry getOreEntry() {
			return oreEntry;
		}

		@Override
		public ItemEntry getItemEntry() {
			return itemEntry;
		}

		//use forge's code here to flow while not trying to spawn items
		public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
			int rarity = ORE_RARITYS.get(oreEntry.getOreName());
			do {
				if(!isSourceBlock(world, pos) && ForgeEventFactory.canCreateFluidSource(world, pos, state, false)) {
					int adjacentSourceBlocks =
							(isSourceBlock(world, pos.north()) ? 1 : 0) +
							(isSourceBlock(world, pos.south()) ? 1 : 0) +
							(isSourceBlock(world, pos.east()) ? 1 : 0) +
							(isSourceBlock(world, pos.west()) ? 1 : 0);
					if(adjacentSourceBlocks >= 2 && (world.getBlockState(pos.up(densityDir)).getMaterial().isSolid() || isSourceBlock(world, pos.up(densityDir))))
						world.setBlockState(pos, state.withProperty(LEVEL, 0));
				}

				int quantaRemaining = quantaPerBlock - state.getValue(LEVEL);
				int expQuanta = -101;

				// check adjacent block levels if non-source
				if(quantaRemaining < quantaPerBlock) {
					if(world.getBlockState(pos.add( 0, -densityDir,  0)).getBlock() == this ||
							world.getBlockState(pos.add(-1, -densityDir,  0)).getBlock() == this ||
							world.getBlockState(pos.add( 1, -densityDir,  0)).getBlock() == this ||
							world.getBlockState(pos.add( 0, -densityDir, -1)).getBlock() == this ||
							world.getBlockState(pos.add( 0, -densityDir,  1)).getBlock() == this) {
						expQuanta = quantaPerBlock - 1;
					}
					else {
						int maxQuanta = -100;
						maxQuanta = getLargerQuanta(world, pos.add(-1, 0,  0), maxQuanta);
						maxQuanta = getLargerQuanta(world, pos.add( 1, 0,  0), maxQuanta);
						maxQuanta = getLargerQuanta(world, pos.add( 0, 0, -1), maxQuanta);
						maxQuanta = getLargerQuanta(world, pos.add( 0, 0,  1), maxQuanta);

						expQuanta = maxQuanta - 1;
					}

					// decay calculation
					if(expQuanta != quantaRemaining) {
						quantaRemaining = expQuanta;

						if(expQuanta <= 0) {
							world.setBlockToAir(pos);
						}
						else {
							world.setBlockState(pos, state.withProperty(LEVEL, quantaPerBlock - expQuanta), 2);
							world.scheduleUpdate(pos, this, tickRate);
							world.notifyNeighborsOfStateChange(pos, this);
						}
					}
				}
				// This is a "source" block, set meta to zero, and send a server only update
				else if(quantaRemaining >= quantaPerBlock) {
					world.setBlockState(pos, this.getDefaultState(), 2);
				}

				// Flow vertically if possible
				if(canDisplace(world, pos.up(densityDir))) {
					flowIntoBlock(world, pos.up(densityDir), 1);
					break;
				}

				// Flow outward if possible
				int flowMeta = quantaPerBlock - quantaRemaining + 1;
				if(flowMeta >= quantaPerBlock) {
					break;
				}

				if(isSourceBlock(world, pos) || !isFlowingVertically(world, pos)) {
					if(world.getBlockState(pos.down(densityDir)).getBlock() == this) {
						flowMeta = 1;
					}
					boolean flowTo[] = getOptimalFlowDirections(world, pos);

					if(flowTo[0]) flowIntoBlock(world, pos.add(-1, 0,  0), flowMeta);
					if(flowTo[1]) flowIntoBlock(world, pos.add( 1, 0,  0), flowMeta);
					if(flowTo[2]) flowIntoBlock(world, pos.add( 0, 0, -1), flowMeta);
					if(flowTo[3]) flowIntoBlock(world, pos.add( 0, 0,  1), flowMeta);
				}
			}
			while(false);

			if(world.isRemote || !isSourceBlock(world, pos) || !isNotFlowing(world, pos, state) || rand.nextInt(rarity) != 0
					|| !ConfigOptions.easyMode && world.getBlockState(pos.down()).getBlock() instanceof CondenserBlock) {
				return;
			}

			ItemStack stack = Utils.getOreStack("shardCrystal", oreEntry, 1);

			Entity entity = new EntityItem(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, stack);
			world.spawnEntity(entity);
			world.playSound(null, pos, SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.BLOCKS, 1.0F, 2.2F/(rand.nextFloat()*0.2F + 0.9F));
			if(rand.nextInt((rarity/2)+8) >= 8) {
				world.setBlockToAir(pos);
			}
		}

		@Override
		public BlockCrystalFluidBase setQuantaPerBlock(int quantaPerBlock) {
			super.setQuantaPerBlock(quantaPerBlock);
			return this;
		}
	}

	public static class BlockDirtyCrystalFluidBase extends FluidCrystalBlock implements IBlockFluidWithProperty {

		public final IOreEntry oreEntry;
		public final ItemEntry itemEntry;

		public BlockDirtyCrystalFluidBase(IFluidWithProperty fluid, Material material) {
			super((Fluid)fluid, material, "", "jaopca:fluid_"+fluid.getItemEntry().name+fluid.getOreEntry().getOreName());
			oreEntry = fluid.getOreEntry();
			itemEntry = fluid.getItemEntry();
			setUnlocalizedName("jaopca."+itemEntry.name);
		}

		@Override
		public IOreEntry getOreEntry() {
			return oreEntry;
		}

		@Override
		public ItemEntry getItemEntry() {
			return itemEntry;
		}

		//use forge's code here to flow while not trying to spawn items
		@Override
		public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
			do {
				if(!isSourceBlock(world, pos) && ForgeEventFactory.canCreateFluidSource(world, pos, state, false)) {
					int adjacentSourceBlocks =
							(isSourceBlock(world, pos.north()) ? 1 : 0) +
							(isSourceBlock(world, pos.south()) ? 1 : 0) +
							(isSourceBlock(world, pos.east()) ? 1 : 0) +
							(isSourceBlock(world, pos.west()) ? 1 : 0);
					if(adjacentSourceBlocks >= 2 && (world.getBlockState(pos.up(densityDir)).getMaterial().isSolid() || isSourceBlock(world, pos.up(densityDir))))
						world.setBlockState(pos, state.withProperty(LEVEL, 0));
				}

				int quantaRemaining = quantaPerBlock - state.getValue(LEVEL);
				int expQuanta = -101;

				// check adjacent block levels if non-source
				if(quantaRemaining < quantaPerBlock) {
					if(world.getBlockState(pos.add( 0, -densityDir,  0)).getBlock() == this ||
							world.getBlockState(pos.add(-1, -densityDir,  0)).getBlock() == this ||
							world.getBlockState(pos.add( 1, -densityDir,  0)).getBlock() == this ||
							world.getBlockState(pos.add( 0, -densityDir, -1)).getBlock() == this ||
							world.getBlockState(pos.add( 0, -densityDir,  1)).getBlock() == this) {
						expQuanta = quantaPerBlock - 1;
					}
					else {
						int maxQuanta = -100;
						maxQuanta = getLargerQuanta(world, pos.add(-1, 0,  0), maxQuanta);
						maxQuanta = getLargerQuanta(world, pos.add( 1, 0,  0), maxQuanta);
						maxQuanta = getLargerQuanta(world, pos.add( 0, 0, -1), maxQuanta);
						maxQuanta = getLargerQuanta(world, pos.add( 0, 0,  1), maxQuanta);

						expQuanta = maxQuanta - 1;
					}

					// decay calculation
					if(expQuanta != quantaRemaining) {
						quantaRemaining = expQuanta;

						if(expQuanta <= 0) {
							world.setBlockToAir(pos);
						}
						else {
							world.setBlockState(pos, state.withProperty(LEVEL, quantaPerBlock - expQuanta), 2);
							world.scheduleUpdate(pos, this, tickRate);
							world.notifyNeighborsOfStateChange(pos, this);
						}
					}
				}
				// This is a "source" block, set meta to zero, and send a server only update
				else if(quantaRemaining >= quantaPerBlock) {
					world.setBlockState(pos, this.getDefaultState(), 2);
				}

				// Flow vertically if possible
				if(canDisplace(world, pos.up(densityDir))) {
					flowIntoBlock(world, pos.up(densityDir), 1);
					break;
				}

				// Flow outward if possible
				int flowMeta = quantaPerBlock - quantaRemaining + 1;
				if(flowMeta >= quantaPerBlock) {
					break;
				}

				if(isSourceBlock(world, pos) || !isFlowingVertically(world, pos)) {
					if(world.getBlockState(pos.down(densityDir)).getBlock() == this) {
						flowMeta = 1;
					}
					boolean flowTo[] = getOptimalFlowDirections(world, pos);

					if(flowTo[0]) flowIntoBlock(world, pos.add(-1, 0,  0), flowMeta);
					if(flowTo[1]) flowIntoBlock(world, pos.add( 1, 0,  0), flowMeta);
					if(flowTo[2]) flowIntoBlock(world, pos.add( 0, 0, -1), flowMeta);
					if(flowTo[3]) flowIntoBlock(world, pos.add( 0, 0,  1), flowMeta);
				}
			}
			while(false);
		}

		@Override
		public BlockDirtyCrystalFluidBase setQuantaPerBlock(int quantaPerBlock) {
			super.setQuantaPerBlock(quantaPerBlock);
			return this;
		}
	}

	public static class BlockMoltenCrystalFluidBase extends FluidMoltenCrystalBlock implements IBlockFluidWithProperty {

		public final IOreEntry oreEntry;
		public final ItemEntry itemEntry;

		public BlockMoltenCrystalFluidBase(IFluidWithProperty fluid, Material material) {
			super((Fluid)fluid, material, "", "jaopca:fluid_"+fluid.getItemEntry().name+fluid.getOreEntry().getOreName());
			oreEntry = fluid.getOreEntry();
			itemEntry = fluid.getItemEntry();
			setUnlocalizedName("jaopca."+itemEntry.name);
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
		public BlockMoltenCrystalFluidBase setQuantaPerBlock(int quantaPerBlock) {
			super.setQuantaPerBlock(quantaPerBlock);
			return this;
		}
	}
}
