package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleNugget extends ModuleBase {

	public static final ItemEntry NUGGET_ENTRY = new ItemEntry(EnumEntryType.ITEM, "nugget", new ModelResourceLocation("jaopca:nugget#inventory"), ImmutableList.<String>of(
			"Gold"
			)).setOreTypes(EnumOreType.DUSTLESS);

	@Override
	public String getName() {
		return "nugget";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(NUGGET_ENTRY);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("nugget")) {
			String s = "ingot";
			switch(entry.getOreType()) {
			case GEM:
			case GEM_ORELESS:
				s = "gem";
				break;
			default:
				break;
			}
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack(s, entry, 1), new Object[] {
					"nugget"+entry.getOreName(),
					"nugget"+entry.getOreName(),
					"nugget"+entry.getOreName(),
					"nugget"+entry.getOreName(),
					"nugget"+entry.getOreName(),
					"nugget"+entry.getOreName(),
					"nugget"+entry.getOreName(),
					"nugget"+entry.getOreName(),
					"nugget"+entry.getOreName(),
			}));

			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("nugget", entry, 9), new Object[] {
					s+entry.getOreName(),
			}));
		}
	}
}
