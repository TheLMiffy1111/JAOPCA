package thelm.jaopca.modules;

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

public class ModulePlate extends ModuleBase {

	public static final ItemEntry PLATE_ENTRY = new ItemEntry(EnumEntryType.ITEM, "plate", new ModelResourceLocation("jaopca:plate#inventory")).
			setOreTypes(EnumOreType.DUSTLESS);

	@Override
	public String getName() {
		return "plate";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(PLATE_ENTRY);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("plate")) {
			if(Loader.isModLoaded("ic2")) {
				switch(entry.getOreType()) {
				case GEM:
				case GEM_ORELESS: {
					if(Utils.doesOreNameExist("dust"+entry.getOreName())) {
						ModuleIndustrialCraft.addCompressorRecipe("dust"+entry.getOreName(), Utils.getOreStack("plate", entry, 1));
					}
					break;
				}
				case INGOT:
				case INGOT_ORELESS: {
					Utils.addShapelessOreRecipe(Utils.getOreStack("plate", entry, 1), new Object[] {
							"ingot"+entry.getOreName(),
							"craftingToolForgeHammer",
					});
					ModuleIndustrialCraft.addRollingRecipe("ingot"+entry.getOreName(), Utils.getOreStack("plate", entry, 1));
					break;
				}
				default:
					break;
				}
				if(Utils.doesOreNameExist("block"+entry.getOreName())) {
					ModuleIndustrialCraft.addBlockCutterRecipe("block"+entry.getOreName(), entry.getEnergyModifier()>1.5?8:5, Utils.getOreStack("plate", entry, 9));
				}
				if(Utils.doesOreNameExist("dustTiny"+entry.getOreName())) {
					ModuleIndustrialCraft.addMaceratorRecipe("plate"+entry.getOreName(), Utils.getOreStack("dustTiny", entry, 8));
				}
			}
		}
	}

	@Override
	public void postInit() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("plate")) {
			if(Loader.isModLoaded("thermalexpansion")) {
				ModuleThermalExpansion.addPressRecipe(Utils.energyI(entry, 4000), Utils.getOreStack("ingot", entry, 1), Utils.getOreStack("plate", entry, 1));
			}
		}
	}
}
