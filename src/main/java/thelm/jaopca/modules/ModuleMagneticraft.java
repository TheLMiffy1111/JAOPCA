package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipeManager;
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipeManager;
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipeManager;
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipeManager;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IItemRequest;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleMagneticraft extends ModuleBase {

	public static final ArrayList<String> CHUNK_BLACKLIST = Lists.<String>newArrayList(
			"Iron", "Gold", "Copper", "Lead", "Cobalt", "Tungsten", "Aluminium", "Mithril", "Nickel", "Osmium", "Silver", "Tin", "Zinc"
			);
	public static final ArrayList<String> ORE_BLACKLIST = Lists.newArrayList(
			"Iron", "Gold", "Copper", "Lead", "Cobalt", "Tungsten", "Steel", "Aluminium", "Galena", "Mithril", "Nickel", "Osmium", "Silver", "Tin", "Zinc", "Redstone", "Lapis",
			"Quartz", "Emerald", "Glowstone", "Sulfur", "Coal"
			);

	public static final ItemEntry ROCKY_CHUNK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "rockyChunk", new ModelResourceLocation("jaopca:rocky_chunk#inventory"), CHUNK_BLACKLIST);
	public static final ItemEntry CHUNK_ENTRY = new ItemEntry(EnumEntryType.ITEM, "chunk", new ModelResourceLocation("jaopca:chunk#inventory"), CHUNK_BLACKLIST);

	@Override
	public String getName() {
		return "magneticraft";
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return EnumSet.<EnumOreType>of(EnumOreType.INGOT_ORELESS, EnumOreType.ORE);
	}

	@Override
	public List<String> getOreBlacklist() {
		return ORE_BLACKLIST;
	}

	@Override
	public List<? extends IItemRequest> getItemRequests() {
		return Lists.<ItemEntryGroup>newArrayList(ItemEntryGroup.of(ROCKY_CHUNK_ENTRY, CHUNK_ENTRY));
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("rockyChunk")) {
			Utils.addSmelting(Utils.getOreStack("rockyChunk", entry, 1), Utils.getOreStack("ingot", entry, 1), 0.1F);
			addCrushingTableRecipe(Utils.getOreStack("ore", entry, 1), Utils.getOreStack("rockyChunk", entry, 1));
			addGrinderRecipe(Utils.getOreStack("ore", entry, 1), Utils.getOreStack("rockyChunk", entry, 1), new ItemStack(Blocks.GRAVEL), 0.15F, 50F);
		}

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("chunk")) {
			Utils.addSmelting(Utils.getOreStack("chunk", entry, 1), Utils.getOreStack("ingot", entry, 2), 0.1F);

			List<Pair<ItemStack, Float>> list = Lists.<Pair<ItemStack, Float>>newArrayList();
			list.add(Pair.<ItemStack, Float>of(Utils.getOreStackExtra("dust", entry, 1), 0.15F));
			if(entry.hasSecondExtra()) {
				list.add(Pair.<ItemStack, Float>of(Utils.getOreStackSecondExtra("dust", entry, 1), 0.15F));
			}
			list.add(Pair.<ItemStack, Float>of(new ItemStack(Blocks.COBBLESTONE), 0.15F));
			addSluiceBoxRecipe(Utils.getOreStack("rockyChunk", entry, 1), Utils.getOreStack("chunk", entry, 1), list);

			List<ItemStack> list1 = Lists.<ItemStack>newArrayList();
			list1.add(Utils.getOreStackExtra("dust", entry, 1));
			if(entry.hasSecondExtra()) {
				list1.add(Utils.getOreStackSecondExtra("dust", entry, 1));
			}
			switch(list1.size()) {
			case 0:
				addSieveRecipe(Utils.getOreStack("rockyChunk", entry, 1), Utils.getOreStack("chunk", entry, 1), 1F, 50F);
				break;
			case 1:
				addSieveRecipe(Utils.getOreStack("rockyChunk", entry, 1), Utils.getOreStack("chunk", entry, 1), 1F, list1.get(0), 0.15F, 50F);
				break;
			case 2:
				addSieveRecipe(Utils.getOreStack("rockyChunk", entry, 1), Utils.getOreStack("chunk", entry, 1), 1F, list1.get(0), 0.15F, list1.get(1), 0.15F, 50F);
			}
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			switch(entry.getOreType()) {
			case DUST:
				addGrinderRecipe(Utils.getOreStack("ore", entry, 1), Utils.getOreStack("dust", entry, 4), new ItemStack(Blocks.GRAVEL), 0.15F, 50F);
				break;
			case GEM:
				addGrinderRecipe(Utils.getOreStack("ore", entry, 1), Utils.getOreStack("gem", entry, 2), new ItemStack(Blocks.GRAVEL), 0.15F, 50F);
				break;
			case INGOT:
			case INGOT_ORELESS:
				if(Utils.doesOreNameExist("dust"+entry.getOreName())) {
					addGrinderRecipe(Utils.getOreStack("ingot", entry, 1), Utils.getOreStack("dust", entry, 1), 50F);
				}
				break;
			default:
				break;
			}
		}
	}

	public static void addCrushingTableRecipe(ItemStack input, ItemStack output) {
		ICrushingTableRecipeManager manager = MagneticraftApi.getCrushingTableRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, output, true));
	}

	public static void addGrinderRecipe(ItemStack input, ItemStack output, float duration) {
		IGrinderRecipeManager manager = MagneticraftApi.getGrinderRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, output, ItemStack.EMPTY, 0F, duration, true));
	}

	public static void addGrinderRecipe(ItemStack input, ItemStack output, ItemStack secondary, float secondaryChance, float duration) {
		IGrinderRecipeManager manager = MagneticraftApi.getGrinderRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, output, secondary, secondaryChance, duration, true));
	}

	public static <L, R> kotlin.Pair<L, R> toKotlinPair(Pair<L, R> pair) {
		return new kotlin.Pair<>(pair.getLeft(), pair.getRight());
	}

	public static void addSluiceBoxRecipe(ItemStack input, ItemStack output) {
		ISluiceBoxRecipeManager manager = MagneticraftApi.getSluiceBoxRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, output, Lists.<kotlin.Pair<ItemStack, Float>>newArrayList(), true));
	}

	public static void addSluiceBoxRecipe(ItemStack input, List<Pair<ItemStack, Float>> outputs) {
		ISluiceBoxRecipeManager manager = MagneticraftApi.getSluiceBoxRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, outputs.stream().map(pair->toKotlinPair(pair)).collect(Collectors.toCollection(Lists::newArrayList)), true));
	}

	public static void addSluiceBoxRecipe(ItemStack input, ItemStack output, List<Pair<ItemStack, Float>> outputs) {
		ISluiceBoxRecipeManager manager = MagneticraftApi.getSluiceBoxRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, output, outputs.stream().map(pair->toKotlinPair(pair)).collect(Collectors.toCollection(Lists::newArrayList)), true));
	}

	public static void addSieveRecipe(ItemStack input, ItemStack output0, float output0Chance, float duration) {
		ISieveRecipeManager manager = MagneticraftApi.getSieveRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, output0, output0Chance, ItemStack.EMPTY, 0F, ItemStack.EMPTY, 0F, duration, true));
	}

	public static void addSieveRecipe(ItemStack input, ItemStack output0, float output0Chance, ItemStack output1, float output1Chance, float duration) {
		ISieveRecipeManager manager = MagneticraftApi.getSieveRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, output0, output0Chance, output1, output1Chance, ItemStack.EMPTY, 0F, duration, true));
	}

	public static void addSieveRecipe(ItemStack input, ItemStack output0, float output0Chance, ItemStack output1, float output1Chance, ItemStack output2, float output2Chance, float duration) {
		ISieveRecipeManager manager = MagneticraftApi.getSieveRecipeManager();
		manager.registerRecipe(manager.createRecipe(input, output0, output0Chance, output1, output1Chance, output2, output2Chance, duration, true));
	}
}
