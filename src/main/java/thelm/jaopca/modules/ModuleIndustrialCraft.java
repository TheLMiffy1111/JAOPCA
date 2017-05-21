package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

public class ModuleIndustrialCraft extends ModuleBase {

	public static final ItemEntry CRUSHED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crushed", new ModelResourceLocation("jaopca:crushed#inventory"), ImmutableList.<String>of(
			"Copper", "Gold", "Iron", "Lead", "Tin", "Silver", "Uranium"
			));
	public static final ItemEntry PURIFIED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "purified", new ModelResourceLocation("jaopca:purified#inventory"), ImmutableList.<String>of(
			"Copper", "Gold", "Iron", "Lead", "Tin", "Silver", "Uranium"
			));
	public static final ItemEntry TINY_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustTiny", new ModelResourceLocation("jaopca:dust_tiny#inventory"), ImmutableList.<String>of(
			"Copper", "Gold", "Iron", "Lead", "Lithium", "Silver", "Tin"
			));

	@Override
	public String getName() {
		return "industrialcraft";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(CRUSHED_ENTRY,PURIFIED_ENTRY,TINY_DUST_ENTRY);
	}

	@Override
	public void init() {
		ItemStack stoneDust = IC2Items.getItem("dust", "stone");

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushed")) {
			JAOPCAApi.LOGGER.debug("crushed"+entry.getOreName());
			addMaceratorRecipe(new RecipeInputOreDict("ore"+entry.getOreName()), Utils.getOreStack("crushed",entry,2));
			addCentrifugeRecipe(new RecipeInputOreDict("crushed"+entry.getOreName()), Utils.energyI(entry, 1500), Utils.getOreStack("dust",entry,1), Utils.getOreStackExtra("dustTiny",entry,2), stoneDust.copy());
			addOreWashingRecipe(new RecipeInputOreDict("crushed"+entry.getOreName()), Utils.getOreStack("purified",entry,1), Utils.getOreStack("dustTiny",entry,2), stoneDust.copy());
			Utils.addSmelting(Utils.getOreStack("crushed", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.2F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("purified")) {
			JAOPCAApi.LOGGER.debug("purified"+entry.getOreName());
			addCentrifugeRecipe(new RecipeInputOreDict("purified"+entry.getOreName()), Utils.energyI(entry, 1500), Utils.getOreStack("dust",entry,1), Utils.getOreStackExtra("dustTiny",entry,1));
			Utils.addSmelting(Utils.getOreStack("purified", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.2F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustTiny")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("dust",entry,1), new Object[] {
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
			}));
		}
	}
	
	public static void addMaceratorRecipe(IRecipeInput input, ItemStack output) {
		Recipes.macerator.addRecipe(input, null, false, output);
	}

	public static void addCentrifugeRecipe(IRecipeInput input, int minHeat, ItemStack... output) {
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("minHeat", minHeat);

		Recipes.centrifuge.addRecipe(input, metadata, false, output);
	}

	public static void addOreWashingRecipe(IRecipeInput input, ItemStack... output) {
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("amount", 1000);

		Recipes.oreWashing.addRecipe(input, metadata, false, output);
	}
}
