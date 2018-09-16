package thelm.jaopca.modules;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleCoin extends ModuleBase {

	public static final HashMap<IOreEntry,Integer> TE_NUMISM_VALUES = Maps.<IOreEntry,Integer>newHashMap();

	public static final ItemEntry COIN_ENTRY = new ItemEntry(EnumEntryType.ITEM, "coin", new ModelResourceLocation("jaopca:coin#inventory")).
			setOreTypes(EnumOreType.INGOTS);

	@Override
	public String getName() {
		return "coin";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(COIN_ENTRY);
	}

	@Override
	public void registerConfigs(Configuration config) {
		if(Loader.isModLoaded("thermalexpansion")) {
			for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("coin")) {
				TE_NUMISM_VALUES.put(entry, config.get(Utils.to_under_score(entry.getOreName()), "teNumismValue", 32000, "Energy generated when the coin of this material is used in the Numismatic Dynamo. (Coin, Thermal Expansion)").setRequiresMcRestart(true).getInt());
			}
		}
	}

	@Override
	public void postInit() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("coin")) {
			if(Loader.isModLoaded("thermalexpansion")) {
				ModuleThermalExpansion.addMintRecipe(Utils.energyI(entry, 4000), Utils.getOreStack("ingot", entry, 1), Utils.getOreStack("coin", entry, 3));
				if(Utils.doesOreNameExist("nugget"+entry.getOreName())) {
					ModuleThermalExpansion.addMintRecipe(Utils.energyI(entry, 1000), Utils.getOreStack("nugget", entry, 3), Utils.getOreStack("coin", entry, 1));
				}
				if(Utils.doesOreNameExist("block"+entry.getOreName())) {
					ModuleThermalExpansion.addMintRecipe(Utils.energyI(entry, 32000), Utils.getOreStack("block", entry, 1), Utils.getOreStack("coin", entry, 27));
				}

				ModuleThermalExpansion.addNumismaticFuel(Utils.getOreStack("coin", entry, 1), TE_NUMISM_VALUES.get(entry));
			}
		}
	}
}
