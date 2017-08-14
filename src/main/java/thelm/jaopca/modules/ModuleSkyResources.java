package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bartz24.skyresources.alchemy.fluid.FluidCrystalBlock;
import com.bartz24.skyresources.alchemy.fluid.FluidRegisterInfo;
import com.bartz24.skyresources.recipe.ProcessRecipeManager;
import com.bartz24.skyresources.registry.ModBlocks;
import com.bartz24.skyresources.registry.ModFluids;
import com.bartz24.skyresources.registry.ModItems;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.EnumEntryType;
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
			"Iron", "Gold", "Copper", "Tin", "Silver", "Zinc", "Nickel", "Platinum", "Aluminum", "Lead", "Mercury", "Quartz", "Cobalt", "Ardite", "Osmium",
			"Lapis", "Draconium", "Certus", "Titanium", "Tungsten", "Chrome", "Iridium", "QuartzBlack", "Boron", "Lithium", "Magnesium", "Mithril"
			);
	public static final HashMap<String,Integer> ORE_RARITYS = Maps.<String,Integer>newHashMap();

	public static final FluidProperties CRYSTAL_FLUID_PROPERTIES = new FluidProperties().
			setBlockFluidClass(BlockCrystalFluidBase.class);
	public static final FluidProperties MOLTEN_CRYSTAL_FLUID_PROPERTIES = new FluidProperties().
			setMaterial(Material.LAVA).
			setBlockFluidClass(BlockCrystalFluidBase.class);

	public static final ItemEntry CRYSTAL_SHARD_ENTRY = new ItemEntry(EnumEntryType.ITEM, "shardCrystal", "shardCrystal", new ModelResourceLocation("jaopca:shard_crystal#inventory"), BLACKLIST);
	public static final ItemEntry CRYSTAL_FLUID_ENTRY = new ItemEntry(EnumEntryType.FLUID, "crystalFluid", new ModelResourceLocation("jaopca:fluids/crystal_fluid#normal"), BLACKLIST).setFluidProperties(CRYSTAL_FLUID_PROPERTIES);
	public static final ItemEntry MOLTEN_CRYSTAL_FLUID_ENTRY = new ItemEntry(EnumEntryType.FLUID, "moltenCrystalFluid", new ModelResourceLocation("jaopca:fluids/molten_crystal_fluid#normal"), BLACKLIST).setFluidProperties(MOLTEN_CRYSTAL_FLUID_PROPERTIES);

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
		for(IOreEntry entry : JAOPCAApi.ORE_ENTRY_LIST) {
			if(!BLACKLIST.contains(entry.getOreName())) {
				if(config.get(Utils.to_under_score(entry.getOreName()), "skyResourcesIsMolten", false).setRequiresMcRestart(true).getBoolean()) {
					CRYSTAL_FLUID_ENTRY.blacklist.add(entry.getOreName());
				}
				else {
					MOLTEN_CRYSTAL_FLUID_ENTRY.blacklist.add(entry.getOreName());
				}
				ORE_RARITYS.put(entry.getOreName(), config.get(Utils.to_under_score(entry.getOreName()), "skyResourcesRarity", 6).setRequiresMcRestart(true).getInt());
			}
		}
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(CRYSTAL_SHARD_ENTRY, CRYSTAL_FLUID_ENTRY, MOLTEN_CRYSTAL_FLUID_ENTRY);
	}

	//WHY
	@Override
	public void postInit() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crystalFluid")) {
			int rarity = ORE_RARITYS.get(entry.getOreName());
			Fluid fluid = JAOPCAApi.FLUIDS_TABLE.get("crystalFluid", entry.getOreName());
			ModFluids.addCrystalFluid(StringUtils.uncapitalize(entry.getOreName()), -1, rarity, FluidRegisterInfo.CrystalFluidType.NORMAL);
			ModFluids.crystalFluids.add(fluid);
			ModBlocks.crystalFluidBlocks.add(fluid.getBlock());

			ProcessRecipeManager.crucibleRecipes.addRecipe(new FluidStack(fluid, 1000), 0F, Utils.getOreStack("shardCrystal", entry, 1));
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("moltenCrystalFluid")) {
			int rarity = ORE_RARITYS.get(entry.getOreName());
			Fluid fluid = JAOPCAApi.FLUIDS_TABLE.get("moltenCrystalFluid", entry.getOreName());
			ModFluids.addCrystalFluid(StringUtils.uncapitalize(entry.getOreName()), -1, rarity, FluidRegisterInfo.CrystalFluidType.MOLTEN);
			ModFluids.crystalFluids.add(fluid);
			ModBlocks.crystalFluidBlocks.add(fluid.getBlock());

			ProcessRecipeManager.crucibleRecipes.addRecipe(new FluidStack(fluid, 1000), 0F, Utils.getOreStack("shardCrystal", entry, 1));
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			int rarity = ORE_RARITYS.get(entry.getOreName());
			ItemStack dust = Utils.getOreStack("dust", entry, 1);

			if(!dust.isEmpty()) {
				if(CRYSTAL_FLUID_ENTRY.blacklist.contains(entry.getOreName())) {
					ProcessRecipeManager.cauldronCleanRecipes.addRecipe(Utils.getOreStack("dust", entry, 1), 1F/(4.4F*rarity), new ItemStack(ModItems.techComponent, 1, 3));
				}
				else {
					ProcessRecipeManager.cauldronCleanRecipes.addRecipe(Utils.getOreStack("dust", entry, 1), 1F/(3.2F*rarity), new ItemStack(ModItems.techComponent, 1, 0));
				}
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

		@Override
		public BlockCrystalFluidBase setQuantaPerBlock(int quantaPerBlock) {
			super.setQuantaPerBlock(quantaPerBlock);
			return this;
		}
	}
}
