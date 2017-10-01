package thelm.jaopca.modules;

import java.util.List;

import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipeManager;
import com.cout970.magneticraft.api.registries.machines.tablesieve.ITableSieveRecipeManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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

	public static final ItemEntry CRUSHED_ORE_ENTRY = new ItemEntry(EnumEntryType.ITEM, "crushedOre", new ModelResourceLocation("jaopca:crushed_ore#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Copper", "Lead", "Cobalt_Mgc", "Tungsten"
			));
	public static final ItemEntry PEBBLES_ENTRY = new ItemEntry(EnumEntryType.ITEM, "pebbles", new ModelResourceLocation("jaopca:pebbles#inventory"), ImmutableList.<String>of(
			"Iron", "Gold", "Copper", "Lead", "Cobalt_Mgc", "Tungsten"
			));

	@Override
	public String getName() {
		return "magneticraft";
	}

	@Override
	public List<? extends IItemRequest> getItemRequests() {
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(CRUSHED_ORE_ENTRY, PEBBLES_ENTRY));
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("crushedOre")) {
			ICrushingTableRecipeManager manager = MagneticraftApi.getCrushingTableRecipeManager();
			manager.registerRecipe(manager.createRecipe(Utils.getOreStack("ore", entry, 1), Utils.getOreStack("crushedOre", entry, 1), true));
			Utils.addSmelting(Utils.getOreStack("crushedOre", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.1F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("pebbles")) {
			ITableSieveRecipeManager manager = MagneticraftApi.getTableSieveRecipeManager();
			manager.registerRecipe(manager.createRecipe(Utils.getOreStack("crushedOre", entry, 1), Utils.getOreStack("pebbles", entry, 1), new ItemStack(Blocks.COBBLESTONE), 0.15F, true));
			Utils.addSmelting(Utils.getOreStack("pebbles", entry, 1), Utils.getOreStack("ingot", entry, 2), 0.1F);
		}
	}
}
