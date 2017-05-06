package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.IRecipeInputFactory;
import ic2.api.recipe.Recipes;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;

public class ModuleIndustrialCraft extends ModuleAbstract {

	public static final ItemEntry CRUSHED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crushed", new ModelResourceLocation("jaopca:crushed#inventory"), ImmutableList.<String>of(
			"Copper", "Gold", "Iron", "Lead", "Tin", "Silver", "Uranium"
			));
	public static final ItemEntry PURIFIED_ENTRY = new ItemEntry(EnumEntryType.ITEM, "purified", new ModelResourceLocation("jaopca:purified#inventory"), ImmutableList.<String>of(
			"Copper", "Gold", "Iron", "Lead", "Tin", "Silver", "Uranium"
			));
	public static final ItemEntry TINY_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustTiny", new ModelResourceLocation("jaopca:dustTiny#inventory"), ImmutableList.<String>of(
			"Copper", "Gold", "Iron", "Lead", "Lithium", "Silver", "Tin"
			));
	
	@Override
	public String getName() {
		return "ic2";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(CRUSHED_ENTRY,PURIFIED_ENTRY,TINY_DUST_ENTRY);
	}

	@Override
	public void registerRecipes() {
		IRecipeInputFactory factory = Recipes.inputFactory;
		ItemStack stoneDust = IC2Items.getItem("dust", "stone");
		
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushed")) {
			Recipes.macerator.addRecipe(factory.forOreDict("ore"+entry.getOreName()), null, true, new ItemStack(JAOPCAApi.ITEMS_TABLE.get("crushed", entry.getOreName()),2,0));
			addCentrifugeRecipe(factory.forOreDict("crushed"+entry.getOreName()), energyI(entry, 1500), getOreStack("dust",entry,1), getOreStackExtra("dustTiny",entry,2), stoneDust.copy());
			addOreWashingRecipe(factory.forOreDict("crushed"+entry.getOreName()), getOreStack("purified",entry,1), getOreStack("dustTiny",entry,2), stoneDust.copy());
			addSmelting(new ItemStack(JAOPCAApi.ITEMS_TABLE.get("crushed", entry.getOreName())), entry.getIngotStack(), 0.2F);
		}
		
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("purified")) {
			addCentrifugeRecipe(factory.forOreDict("purified"+entry.getOreName()), energyI(entry, 1500), getOreStack("dust",entry,1), getOreStackExtra("dustTiny",entry,1));
			addSmelting(new ItemStack(JAOPCAApi.ITEMS_TABLE.get("purified", entry.getOreName())), entry.getIngotStack(), 0.2F);
		}
		
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustTiny")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(getOreStack("dust",entry,1), new Object[] {
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
	
	public static void addCentrifugeRecipe(IRecipeInput input, int minHeat, ItemStack... output) {
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("minHeat", minHeat);

		Recipes.centrifuge.addRecipe(input, metadata, true, output);
	}

	public static void addOreWashingRecipe(IRecipeInput input, ItemStack... output) {
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("amount", 1000);

		Recipes.oreWashing.addRecipe(input, metadata, true, output);
	}
}
