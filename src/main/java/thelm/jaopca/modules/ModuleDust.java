package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.ModuleAbstract;

public class ModuleDust extends ModuleAbstract {

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
				addSmelting(dust.copy(), entry.getIngotStack(), 0);
			}
		}
	}
}
