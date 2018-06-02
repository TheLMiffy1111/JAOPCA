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
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleTinyDust extends ModuleBase {

	public static final ItemEntry TINY_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustTiny", new ModelResourceLocation("jaopca:dust_tiny#inventory")).
			skipWhenGrouped(Loader.isModLoaded("techreborn")).
			setOreTypes(EnumOreType.DUSTLESS);

	public static final ArrayList<String> IC2_BLACKLIST = Lists.newArrayList("Copper", "Gold", "Iron", "Lead", "Lithium", "Silver", "Tin", "Lapis", "Bronze");

	@Override
	public String getName() {
		return "tinydust";
	}

	@Override
	public List<ItemEntryGroup> getItemRequests() {
		if(Loader.isModLoaded("ic2")) {
			TINY_DUST_ENTRY.blacklist.addAll(IC2_BLACKLIST);
		}
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(TINY_DUST_ENTRY));
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustTiny")) {
			Utils.addShapelessOreRecipe(Utils.getOreStack("dust", entry, 1), new Object[] {
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
					"dustTiny"+entry.getOreName(),
			});
		}
	}
}
