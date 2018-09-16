package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchStage;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleThaumcraft extends ModuleBase {

	public static final String SPECIAL_MINING_MESSAGE = "%d,%d,%d,%d,%f";

	public static final ItemEntry NATIVE_CLUSTER_ENTRY = new ItemEntry(EnumEntryType.ITEM, "cluster", new ModelResourceLocation("jaopca:cluster#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Copper", "Tin", "Silver", "Lead", "Cinnabar", "Quartz"
			));

	@Override
	public String getName() {
		return "thaumcraft";
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return Utils.<EnumOreType>enumSetOf(EnumOreType.ORE);
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Diamond", "Redstone", "Lapis", "Emerald", "Gold", "Iron", "Cinnabar", "Copper", "Tin", "Silver", "Lead", "Quartz"
				);
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(NATIVE_CLUSTER_ENTRY);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("cluster")) {
			Utils.addSmelting(Utils.getOreStack("cluster", entry, 1), Utils.getOreStack("ingot", entry, 2), 1F);
			if(Utils.doesOreNameExist("nugget"+entry.getOreName())) {
				addSmeltingBonus(Utils.getOreStack("cluster", entry, 1), Utils.getOreStack("nugget", entry, 1));
			}
			addSmeltingBonus(Utils.getOreStack("cluster", entry, 1), new ItemStack(ItemsTC.nuggets, 1, 10), 0.02F);
			for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
				addSpecialMiningResult(ore, Utils.getOreStack("cluster", entry, 1), 1F);
			}
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(Utils.doesOreNameExist("nugget"+entry.getOreName())) {
				addSmeltingBonus("ore"+entry.getOreName(), Utils.getOreStack("nugget", entry, 1));
			}
			addSmeltingBonus("ore"+entry.getOreName(), new ItemStack(ItemsTC.nuggets, 1, 10), (float)(entry.getRarity()*0.01D));
		}
	}

	@Override
	public void postInit() {
		//Put the recipes directly into Metal Purification research
		ArrayList<ResourceLocation> addedRecipes = Lists.<ResourceLocation>newArrayList();
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("cluster")) {
			ResourceLocation rl = new ResourceLocation("jaopca:metal_purification_"+Utils.to_under_score(entry.getOreName()));
			ThaumcraftApi.addCrucibleRecipe(rl, new CrucibleRecipe("METALPURIFICATION", Utils.getOreStack("cluster", entry, 1), "ore"+entry.getOreName(), new AspectList().merge(Aspect.METAL, 5).merge(Aspect.ORDER, 5)));
			addedRecipes.add(rl);
		}
		try {
			ResearchStage stage = ResearchCategories.getResearch("METALPURIFICATION").getStages()[0];
			ArrayList<ResourceLocation> recipes = Lists.<ResourceLocation>newArrayList(stage.getRecipes());
			recipes.addAll(addedRecipes);
			stage.setRecipes(recipes.toArray(new ResourceLocation[recipes.size()]));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void addSmeltingBonus(Object in, ItemStack out) {
		ThaumcraftApi.addSmeltingBonus(in, out);
	}

	public static void addSmeltingBonus(Object in, ItemStack out, float chance) {
		ThaumcraftApi.addSmeltingBonus(in, out, chance);
	}

	public static void addSpecialMiningResult(ItemStack in, ItemStack out, float chance) {
		FMLInterModComms.sendMessage("thaumcraft", "nativeCluster", String.format(SPECIAL_MINING_MESSAGE,
				Item.getIdFromItem(in.getItem()), in.getItemDamage(), Item.getIdFromItem(out.getItem()), out.getItemDamage(), chance));
	}
}
