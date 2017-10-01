package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleStick extends ModuleBase {

	public static final ItemEntry STICK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "stick", new ModelResourceLocation("jaopca:stick#inventory")).
			setOreTypes(EnumOreType.DUSTLESS);

	@Override
	public String getName() {
		return "stick";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(STICK_ENTRY);
	}

	@Override
	public void preInit() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("stick")) {
			OreDictionary.registerOre("rod"+entry.getOreName(), Utils.getOreStack("stick", entry, 1));
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("stick")) {
			String s = "ingot";
			switch(entry.getOreType()) {
			case GEM:
			case GEM_ORELESS:
				s = "gem";
				break;
			default:
				break;
			}
			Utils.addShapedOreRecipe(Utils.getOreStack("stick", entry, 4), new Object[] {
					"o",
					"o",
					'o', s+entry.getOreName(),
			});
		}
	}
}
