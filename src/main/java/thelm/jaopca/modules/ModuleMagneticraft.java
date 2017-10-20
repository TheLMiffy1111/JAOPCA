package thelm.jaopca.modules;

import java.util.List;

import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipeManager;
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipeManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import kotlin.Pair;
import kotlin.TuplesKt;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IItemRequest;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleMagneticraft extends ModuleBase {

	public static final ItemEntry ROCKY_CHUNK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "rockyChunk", new ModelResourceLocation("jaopca:rocky_chunk#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Copper", "Lead", "Cobalt", "Tungsten", "Aluminium", "Mithril", "Nickel", "Osmium", "Silver", "Tin", "Zinc"
			));
	public static final ItemEntry CHUNK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "chunk", new ModelResourceLocation("jaopca:chunk#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Copper", "Lead", "Cobalt", "Tungsten", "Aluminium", "Mithril", "Nickel", "Osmium", "Silver", "Tin", "Zinc"
			));

	@Override
	public String getName() {
		return "magneticraft";
	}

	@Override
	public List<? extends IItemRequest> getItemRequests() {
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(ROCKY_CHUNK_ENTRY, CHUNK_ENTRY));
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("rockyChunk")) {
			ICrushingTableRecipeManager manager = MagneticraftApi.getCrushingTableRecipeManager();
			manager.registerRecipe(manager.createRecipe(Utils.getOreStack("ore", entry, 1), Utils.getOreStack("rockyChunk", entry, 1), true));
			Utils.addSmelting(Utils.getOreStack("rockyChunk", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.1F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("chunk")) {
			ISluiceBoxRecipeManager manager = MagneticraftApi.getSluiceBoxRecipeManager();
			List<Pair<ItemStack, Float>> list = Lists.<Pair<ItemStack, Float>>newArrayList();
			list.add(TuplesKt.to(Utils.getOreStackExtra("dust", entry, 1), 0.15F));
			if(entry.hasSecondExtra()) {
				list.add(TuplesKt.to(Utils.getOreStackSecondExtra("dust", entry, 1), 0.15F));
			}
			list.add(TuplesKt.to(new ItemStack(Blocks.COBBLESTONE), 0.15F));
			manager.registerRecipe(manager.createRecipe(Utils.getOreStack("rockyChunk", entry, 1), Utils.getOreStack("chunk", entry, 1), list, true));
			Utils.addSmelting(Utils.getOreStack("chunk", entry, 1), Utils.getOreStack("ingot", entry, 2), 0.1F);
		}
	}
}
