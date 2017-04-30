package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;

public class ModuleNugget extends ModuleAbstract {

	public static final ItemEntry NUGGET_ENTRY = new ItemEntry(EnumEntryType.ITEM, "nugget", new ModelResourceLocation("jaopca:nugget#inventory"), ImmutableList.<String>of("Gold"));

	@Override
	public String getName() {
		return "nugget";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(NUGGET_ENTRY);
	}

	@Override
	public void registerRecipes() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("nugget")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(entry.getIngotStack().copy(), new Object[] {
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

			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(JAOPCAApi.ITEMS_TABLE.get("nugget", entry.getOreName()),9,0), new Object[] {
					"ingot"+entry.getOreName(),
			}));
		}
	}
}
