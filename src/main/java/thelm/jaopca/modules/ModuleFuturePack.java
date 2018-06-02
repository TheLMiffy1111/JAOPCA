package thelm.jaopca.modules;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import futurepack.common.crafting.FPZentrifugeManager;
import futurepack.common.crafting.ZentrifugeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleFuturePack extends ModuleBase {

	public static final HashMap<IOreEntry,String> ORE_SECONDARIES = Maps.<IOreEntry,String>newHashMap();

	@Override
	public String getName() {
		return "futurepack";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return Utils.<EnumOreType>enumSetOf(EnumOreType.ORE);
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Iron", "Gold", "Tin", "Copper", "Zinc", "Magnetite", "Silver", "Lead", "Iridium", "Titanium", "Unobtainium",
				"Tungsten", "Platinum", "Cobalt", "Naquadah", "Nickel", "Molybdenum", "Wulfenit", "Beryllium", "Manganese",
				"Magnesium", "DevilsIron", "Diamond", "Emerald", "Lapis", "Quartz", "Ruby", "Amethyst", "Cinnabar", "Apatite",
				"Olivine", "Coal", "Sulfur", "Pyrite"
				);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			String value = config.get(Utils.to_under_score(entry.getOreName()), "futurePackSecondary", "minecraft:cobblestone@0x3").setRequiresMcRestart(true).getString();
			ORE_SECONDARIES.put(entry, value);
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			switch(entry.getOreType()) {
			case DUST: {
				for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
					addCentrifugeRecipe(Utils.resizeStack(ore, 4), new ItemStack[] {
							Utils.getOreStack("dust", entry, 12),
							Utils.parseItemStack(ORE_SECONDARIES.get(entry)),
							getExtraStack(entry)
					}, Utils.energyI(entry, 6), Utils.energyI(entry, 200));
				}
				break;
			}
			case GEM: {
				for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
					addCentrifugeRecipe(Utils.resizeStack(ore, 4), new ItemStack[] {
							Utils.getOreStack("gem", entry, 12),
							Utils.parseItemStack(ORE_SECONDARIES.get(entry)),
							getExtraStack(entry)
					}, Utils.energyI(entry, 6), Utils.energyI(entry, 200));
				}
				break;
			}
			case INGOT: {
				for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
					addCentrifugeRecipe(Utils.resizeStack(ore, 4), new ItemStack[] {
							Utils.getOreStack("dust", entry, 10),
							Utils.parseItemStack(ORE_SECONDARIES.get(entry)),
							getExtraStack(entry)
					}, Utils.energyI(entry, 6), Utils.energyI(entry, 200));
				}
				break;
			}
			default:
				break;
			}
		}
	}

	public static ItemStack getExtraStack(IOreEntry entry) {
		switch(Utils.oreNameToType(entry.getExtra())) {
		case GEM:
			return Utils.getOreStackExtra("gem", entry, 1);
		case INGOT:
		case DUST:
			return Utils.getOreStackExtra("dust", entry, 3);
		default:
			break;
		}
		return ItemStack.EMPTY;
	}

	public static void addCentrifugeRecipe(ItemStack in, ItemStack[] out, int support, int time) {
		ZentrifugeRecipe recipe = FPZentrifugeManager.instance.addZentrifugeRecipe(in, out, support);
		recipe.setTime(time);
	}
}
