package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleDust extends ModuleBase {

	public static final ItemEntry DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dust", new ModelResourceLocation("jaopca:dust#inventory")).
			setOreTypes(EnumOreType.DUSTLESS);

	@Override
	public String getName() {
		return "dust";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.newArrayList(DUST_ENTRY);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dust")) {
			switch(entry.getOreType()) {
			case INGOT:
			case INGOT_ORELESS: {
				for(ItemStack dust : OreDictionary.getOres("dust"+entry.getOreName())) {
					Utils.addSmelting(dust.copy(), Utils.getOreStack("ingot", entry, 1), 0);
				}
				break;
			}
			default:
				break;
			}
		}
	}
}
