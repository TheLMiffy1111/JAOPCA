package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IModule;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

public class ModuleDust implements IModule {

	public static final ItemEntry DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dust", new ModelResourceLocation("jaopca:dust#inventory"));

	@Override
	public String getName() {
		return "dust";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.newArrayList(DUST_ENTRY);
	}

	@Override
	public void registerRecipes() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dust")) {
			for(ItemStack dust : OreDictionary.getOres("dust"+entry.getOreName())) {
				Utils.addSmelting(dust.copy(), Utils.getOreStack("ingot", entry, 1), 0);
			}
		}
	}
}
