package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import buildcraft.api.recipes.BuildcraftRecipeRegistry;
import buildcraft.api.recipes.IRefineryRecipeManager;
import kotlin.TuplesKt;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.ndrei.bcoreprocessing.api.recipes.IFluidProcessorRecipeManager;
import net.ndrei.bcoreprocessing.api.recipes.IOreProcessorRecipeManager;
import net.ndrei.bcoreprocessing.api.recipes.OreProcessingRecipes;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.fluid.FluidProperties;
import thelm.jaopca.api.utils.Utils;

public class ModuleBuildcraftOreProcessing extends ModuleBase {

	//I'm sure there are alloys in this list
	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Adamantine", "Antimony", "Aquarium", "Bismuth", "Brass", "Bronze", "Coldiron", "Copper", "Cupronickel",
			"Electrum", "Invar", "Lead", "Mercury", "Mithril", "Nickel", "Pewter", "Platinum", "Silver", "Starsteel", "Tin", "Zinc",
			"Aluminium", "AluminiumBrass", "Cadmium", "GalvanizedSteel", "Iridium", "Magnesium", "Manganese", "Nichrome", "Osmium",
			"Plutonium", "Rutile", "StainlessSteel", "Tantalum", "Titanium", "Tungsten", "Uranium", "Zirconium"
			);

	public static final FluidProperties SEARING_MOLTEN_PROPERTIES = new FluidProperties().
			setTemperatureFunc(entry->1300).
			setFillSound(SoundEvents.ITEM_BUCKET_FILL_LAVA).
			setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY_LAVA).
			setMaterial(Material.LAVA);
	public static final FluidProperties HOT_MOLTEN_PROPERTIES = new FluidProperties().
			setTemperatureFunc(entry->800).
			setFillSound(SoundEvents.ITEM_BUCKET_FILL_LAVA).
			setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY_LAVA).
			setMaterial(Material.LAVA);
	public static final FluidProperties COOL_MOLTEN_PROPERTIES = new FluidProperties().
			setFillSound(SoundEvents.ITEM_BUCKET_FILL_LAVA).
			setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY_LAVA).
			setMaterial(Material.LAVA);

	public static final ItemEntry SEARING_MOLTEN_ENTRY = new ItemEntry(EnumEntryType.FLUID, "searingMolten", new ModelResourceLocation("jaopca:fluids/searing_molten#normal"), BLACKLIST).
			setProperties(SEARING_MOLTEN_PROPERTIES);
	public static final ItemEntry HOT_MOLTEN_ENTRY = new ItemEntry(EnumEntryType.FLUID, "hotMolten", new ModelResourceLocation("jaopca:fluids/hot_molten#normal"), BLACKLIST).
			setProperties(HOT_MOLTEN_PROPERTIES);
	public static final ItemEntry COOL_MOLTEN_ENTRY = new ItemEntry(EnumEntryType.FLUID, "coolMolten", new ModelResourceLocation("jaopca:fluids/cool_molten#normal"), BLACKLIST).
			setProperties(COOL_MOLTEN_PROPERTIES);

	@Override
	public String getName() {
		return "buildcraftoreprocessing";
	}

	@Override
	public List<ItemEntryGroup> getItemRequests() {
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(COOL_MOLTEN_ENTRY, HOT_MOLTEN_ENTRY, SEARING_MOLTEN_ENTRY));
	}

	@Override
	public void init() {
		Fluid gasLava = FluidRegistry.getFluid("bcop-gaseous_lava-searing");
		IRefineryRecipeManager rManager = BuildcraftRecipeRegistry.refineryRecipes;
		IOreProcessorRecipeManager oManager = OreProcessingRecipes.INSTANCE.getOreProcessorRecipes();
		IFluidProcessorRecipeManager fManager = OreProcessingRecipes.INSTANCE.getFluidProcessorRecipes();
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("searingMolten")) {
			rManager.addCoolableRecipe(Utils.getFluidStack("searingMolten", entry, 1000), Utils.getFluidStack("hotMolten", entry, 1000), 2, 1);

			for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
				oManager.registerSimpleRecipe(ore, TuplesKt.to(Utils.getFluidStack("searingMolten", entry, 1000), new FluidStack(gasLava, 125)), 40);
			}

			fManager.registerSimpleRecipe(Utils.getFluidStack("searingMolten", entry, 1000), Utils.getOreStack("ingot", entry, 1), new FluidStack(gasLava, 50), 40);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("hotMolten")) {
			rManager.addCoolableRecipe(Utils.getFluidStack("hotMolten", entry, 1000), Utils.getFluidStack("coolMolten", entry, 1000), 1, 0);
			rManager.addHeatableRecipe(Utils.getFluidStack("hotMolten", entry, 1000), Utils.getFluidStack("searingMolten", entry, 1000), 1, 2);

			fManager.registerSimpleRecipe(Utils.getFluidStack("hotMolten", entry, 1000), Utils.getOreStack("ingot", entry, 2), new FluidStack(gasLava, 25), 40);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("coolMolten")) {
			rManager.addHeatableRecipe(Utils.getFluidStack("coolMolten", entry, 1000), Utils.getFluidStack("hotMolten", entry, 1000), 1, 0);

			fManager.registerSimpleRecipe(Utils.getFluidStack("coolMolten", entry, 1000), Utils.getOreStack("ingot", entry, 3), new FluidStack(gasLava, 10), 40);
		}
	}
}
