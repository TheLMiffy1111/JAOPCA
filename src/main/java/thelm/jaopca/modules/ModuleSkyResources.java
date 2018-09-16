package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bartz24.skyresources.recipe.ProcessRecipeManager;
import com.bartz24.skyresources.registry.ModFluids;
import com.bartz24.skyresources.registry.ModItems;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleSkyResources extends ModuleBase {

	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Copper", "Tin", "Silver", "Zinc", "Nickel", "Platinum", "Aluminium", "Lead", "Cobalt", "Ardite",
			"Osmium", "Draconium", "Titanium", "Tungsten", "Chromium", "Iridium", "Boron", "Lithium", "Magnesium", "Mithril",
			"Yellorium", "Uranium", "Thorium"
			);
	public static final ArrayList<String> GEM_BLACKLIST = Lists.<String>newArrayList(
			"Emerald", "Diamond", "Ruby", "Sapphire", "Peridot", "RedGarnet", "YellowGarnet", "Apatite", "Amber", "Lepidolite", "Malachite", "Onyx",
			"Moldavite", "Agate", "Opal", "Amethyst", "Jasper", "Aquamarine", "Heliodor", "Turquoise", "Moonstone", "Morganite", "Carnelian", "Beryl",
			"GoldenBeryl", "Citrine", "Indicolite", "Garnet", "Topaz", "Ametrine", "Tanzanite", "VioletSapphire", "Alexandrite", "BlueTopaz", "Spinel",
			"Iolite", "BlackDiamond", "Chaos", "EnderEssence", "Dark", "Quartz", "Lapis", "QuartzBlack", "CertusQuartz"
			);

	public static final HashMap<IOreEntry, ItemStack> ORE_CLEAN_BASES = Maps.<IOreEntry, ItemStack>newHashMap();
	public static final HashMap<IOreEntry, ItemStack> ORE_BASES = Maps.<IOreEntry, ItemStack>newHashMap();

	public static final ItemEntry ALCH_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustAlch", new ModelResourceLocation("jaopca:dust_alch#inventory"), BLACKLIST);
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
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(ALCH_DUST_ENTRY, DIRTY_GEM_ENTRY);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dirtyGem")) {
			ORE_BASES.put(entry, Utils.parseItemStack(config.get(Utils.to_under_score(entry.getOreName()), "skyResourcesBase", "minecraft:stone", "The block used to create the ore. (Sky Resources)").setRequiresMcRestart(true).getString()));
		}
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustAlch")) {
			ORE_BASES.put(entry, Utils.parseItemStack(config.get(Utils.to_under_score(entry.getOreName()), "skyResourcesBase", "minecraft:stone", "The block to grind for the material. (Sky Resources)").setRequiresMcRestart(true).getString()));
		}
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			ORE_CLEAN_BASES.put(entry, Utils.parseItemStack(config.get(Utils.to_under_score(entry.getOreName()), "skyResourcesCleanBase", "skyresources:techitemcomponent@0", "The item that can be cleaned for this material. (Sky Resources)").setRequiresMcRestart(true).getString()));
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustAlch")) {
			int rarity = Utils.rarityI(entry, 6);
			ItemStack base = ORE_BASES.get(entry);
			if(Utils.doesOreNameExist("dust"+entry.getOreName())) {
				ProcessRecipeManager.fusionRecipes.addRecipe(Utils.getOreStack("dustAlch", entry, 1), rarity*0.0021F, Lists.newArrayList("dust"+entry.getOreName(), getOreItemDust(rarity)));
			}
			ProcessRecipeManager.condenserRecipes.addRecipe(Utils.getOreStack("ingot", entry, 1), (float)Math.pow(1.4D, rarity)*50, Lists.newArrayList(Utils.getOreStack("dustAlch", entry, 1), new FluidStack(ModFluids.crystalFluid, 1000)));
			ProcessRecipeManager.condenserRecipes.addRecipe(Utils.getOreStack("ore", entry, 1), (float)Math.pow(1.8D, rarity)*50, Lists.newArrayList(Utils.getOreStack("dustAlch", entry, 1), base));
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dirtyGem")) {
			float rarity = Utils.rarityReciprocalF(entry, 0.006D);
			ItemStack base = ORE_BASES.get(entry);
			ProcessRecipeManager.rockGrinderRecipes.addRecipe(Utils.getOreStack("dirtyGem", entry, 1), rarity, base);
			//same thing right
			ProcessRecipeManager.cauldronCleanRecipes.addRecipe(Utils.getOreStack("gem", entry, 1), 1F, Utils.getOreStack("dirtyGem", entry, 1));
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			int rarity = Utils.rarityI(entry, 6);
			ItemStack base = ORE_CLEAN_BASES.get(entry);
			if(Utils.doesOreNameExist("dust"+entry.getOreName())) {
				if(base!=null&&!base.isEmpty()) {
					ProcessRecipeManager.cauldronCleanRecipes.addRecipe(Utils.getOreStack("dust", entry, 1), 1.0F/(float)Math.pow(rarity+2.5F, 3.7F), base);
				}
			}
		}
	}

	private static ItemStack getOreItemDust(int rarity) {
		if(rarity <= 2) {
			return new ItemStack(Items.GUNPOWDER, 2);
		}
		if(rarity <= 4) {
			return new ItemStack(Items.BLAZE_POWDER, 2);
		}
		if(rarity <= 6) {
			return new ItemStack(Items.GLOWSTONE_DUST, 2);
		}
		if(rarity <= 8) {
			return new ItemStack(Items.DYE, 2, 4);
		}
		return new ItemStack(ModItems.baseComponent, 2, 3);
	}
}
