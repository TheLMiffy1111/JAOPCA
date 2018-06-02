package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleSmallDust extends ModuleBase {

	public static final ItemEntry SMALL_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustSmall", new ModelResourceLocation("jaopca:dust_small#inventory")).
			setOreTypes(EnumOreType.values());

	public static final ArrayList<String> TECH_REBORN_BLACKLIST = Lists.<String>newArrayList(
			"Almandine", "Aluminium", "Andradite", "Ashes", "Basalt", "Bauxite", "Brass", "Bronze", "Calcite", "Chromium", "Cinnabar", "Coal", "Copper",
			"Diamond", "Electrum", "Emerald", "Galena", "Gold", "Grossular", "Invar", "Iron", "Lazurite", "Lead", "Magnesium", "Manganese",
			"Nickel", "Peridot", "Phosphorous", "Platinum", "Pyrite", "Pyrope", "RedGarnet", "Ruby", "Saltpeter", "Sapphire", "Silver", "Sodalite",
			"Spessartine", "Sphalerite", "Steel", "Sulfur", "Tin", "Titanium", "Tungsten", "Uvarovite", "YellowGarnet", "Zinc", "Redstone", "Glowstone"
			);

	@Override
	public String getName() {
		return "smalldust";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		if(Loader.isModLoaded("techreborn")) {
			SMALL_DUST_ENTRY.blacklist.addAll(TECH_REBORN_BLACKLIST);
		}
		return Lists.<ItemEntry>newArrayList(SMALL_DUST_ENTRY);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustSmall")) {
			Utils.addShapelessOreRecipe(Utils.getOreStack("dust", entry, 1), new Object[] {
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
			});

			Utils.addShapelessOreRecipe(Utils.getOreStack("dustSmall", entry, 4), new Object[] {
					"dust"+entry.getOreName(),
			});
		}
	}
}
