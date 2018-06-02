package thelm.jaopca.modules;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputFluidContainer;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleIndustrialCraft extends ModuleBase {

	public static final ItemEntry CRUSHED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crushed", new ModelResourceLocation("jaopca:crushed#inventory"), ImmutableList.<String>of(
			"Copper", "Gold", "Iron", "Lead", "Tin", "Silver", "Uranium"
			));
	public static final ItemEntry PURIFIED_CRUSHED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crushedPurified", new ModelResourceLocation("jaopca:crushed_purified#inventory"), ImmutableList.<String>of(
			"Copper", "Gold", "Iron", "Lead", "Tin", "Silver", "Uranium"
			));

	@Override
	public String getName() {
		return "industrialcraft";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust", "tinydust");
	}

	@Override
	public List<ItemEntryGroup> getItemRequests() {
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(CRUSHED_ENTRY, PURIFIED_CRUSHED_ENTRY));
	}

	@Override
	public void init() {
		ItemStack stoneDust = IC2Items.getItem("dust", "stone");

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushed")) {
			addMaceratorRecipe(new RecipeInputOreDict("ore"+entry.getOreName()), Utils.getOreStack("crushed",entry,2));
			addCentrifugeRecipe(new RecipeInputOreDict("crushed"+entry.getOreName()), Utils.energyI(entry, 1500), Utils.getOreStack("dust",entry,1), Utils.getOreStackExtra("dustTiny",entry,2), stoneDust.copy());
			addOreWashingRecipe(new RecipeInputOreDict("crushed"+entry.getOreName()), Utils.getOreStack("crushedPurified",entry,1), Utils.getOreStack("dustTiny",entry,2), stoneDust.copy());
			Utils.addSmelting(Utils.getOreStack("crushed", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.2F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushedPurified")) {
			addCentrifugeRecipe(new RecipeInputOreDict("crushedPurified"+entry.getOreName()), Utils.energyI(entry, 1500), Utils.getOreStack("dust",entry,1), Utils.getOreStackExtra("dustTiny",entry,1));
			Utils.addSmelting(Utils.getOreStack("crushedPurified", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.2F);
		}
	}

	@Override
	public List<Pair<String, String>> remaps() {
		return Lists.<Pair<String, String>>newArrayList(
				Pair.of("purified", "crushedPurified")
				);
	}

	public static IRecipeInput getRecipeInput(Object input) {
		IRecipeInput ri = null;
		if(input instanceof String) {
			ri = new RecipeInputOreDict((String)input);
		}
		if(input instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>)input;
			if(pair.getLeft() instanceof String) {
				if(pair.getRight() instanceof Integer) {
					ri = new RecipeInputOreDict((String)pair.getLeft(), (Integer)pair.getRight());
				}
			}
		}
		if(input instanceof ItemStack) {
			ri = new RecipeInputItemStack((ItemStack)input);
		}
		if(input instanceof Fluid) {
			ri = new RecipeInputFluidContainer((Fluid)input);
		}
		if(input instanceof FluidStack) {
			FluidStack fs = (FluidStack)input;
			ri = new RecipeInputFluidContainer(fs.getFluid(), fs.amount);
		}
		if(input instanceof IRecipeInput) {
			ri = (IRecipeInput)input;
		}
		return ri;
	}

	public static void addMaceratorRecipe(Object input, ItemStack output) {
		Recipes.macerator.addRecipe(getRecipeInput(input), null, false, output);
	}

	public static void addCentrifugeRecipe(Object input, int minHeat, ItemStack... output) {
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("minHeat", minHeat);
		Recipes.centrifuge.addRecipe(getRecipeInput(input), metadata, false, output);
	}

	public static void addOreWashingRecipe(Object input, ItemStack... output) {
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("amount", 1000);
		Recipes.oreWashing.addRecipe(getRecipeInput(input), metadata, false, output);
	}

	public static void addCompressorRecipe(Object input, ItemStack output) {
		Recipes.compressor.addRecipe(getRecipeInput(input), null, false, output);
	}

	public static void addRollingRecipe(Object input, ItemStack output) {
		Recipes.metalformerRolling.addRecipe(getRecipeInput(input), null, false, output);
	}

	public static void addBlockCutterRecipe(Object input, int hardness, ItemStack output) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("hardness", hardness);
		Recipes.blockcutter.addRecipe(getRecipeInput(input), nbt, false, output);
	}
}
