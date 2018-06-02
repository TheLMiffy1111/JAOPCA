package thelm.jaopca.modules;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

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

public class ModuleDensePlate extends ModuleBase {

	public static final ItemEntry DENSE_PLATE_ENTRY = new ItemEntry(EnumEntryType.ITEM, "plateDense", new ModelResourceLocation("jaopca:dense_plate#inventory")).
			setOreTypes(EnumOreType.DUSTLESS);

	@Override
	public String getName() {
		return "denseplate";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(DENSE_PLATE_ENTRY);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("plateDense")) {
			if(Loader.isModLoaded("IC2")) {
				ModuleIndustrialCraft.addCompressorRecipe(Pair.of("plate"+entry.getOreName(), 9), Utils.getOreStack("plateDense", entry, 1));
				if(Utils.doesOreNameExist("dust"+entry.getOreName())) {
					ModuleIndustrialCraft.addMaceratorRecipe("plateDense"+entry.getOreName(), Utils.getOreStack("dust", entry, 8));
				}
			}
		}
	}
}
