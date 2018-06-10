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

	@Override
	public String getName() {
		return "smalldust";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.newArrayList("dust");
	}

	@Override
	public List<ItemEntry> getItemRequests() {
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

			if(Utils.doesOreNameExist("dustTiny"+entry.getOreName())) {
				Utils.addShapedOreRecipe(Utils.getOreStack("dustSmall", entry, 4), new Object[] {
						"  ",
						" D",
						'D', "dust"+entry.getOreName(),
				});
			}
			else {
				Utils.addShapelessOreRecipe(Utils.getOreStack("dustSmall", entry, 4), new Object[] {
						"dust"+entry.getOreName(),
				});
			}
		}
	}
}
