package thelm.jaopca.modules;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import minestrapp.crafting.CrusherRecipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleMinestrappolation extends ModuleBase {

	public static final HashMap<IOreEntry, ItemStack> ORE_EXTRAS = Maps.<IOreEntry, ItemStack>newHashMap();
	public static final HashMap<IOreEntry, Integer> ORE_CHANCES = Maps.<IOreEntry, Integer>newHashMap();

	@Override
	public String getName() {
		return "minestrappolation";
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return Utils.<EnumOreType>enumSetOf(EnumOreType.ORE);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			ORE_EXTRAS.put(entry, Utils.parseItemStack(config.get(Utils.to_under_score(entry.getOreName()), "minestrappExtra", "minestrapp:m_chunks@1", "The byproduct of the ore in the crusher. (Minestrappolation)").setRequiresMcRestart(true).getString()));
			ORE_CHANCES.put(entry, config.get(Utils.to_under_score(entry.getOreName()), "minestrappExtraChance", 40, "The chance of the byproduct in the crusher. (Minestrappolation)").setRequiresMcRestart(true).getInt());
		}
	}

	@Override
	public void init() {
		CrusherRecipes recipes = CrusherRecipes.instance();
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			List<ItemStack> ores = Utils.getOres("ore"+entry.getOreName()).stream().filter(stack->!stack.getItem().getRegistryName().getResourceDomain().equals("minestrapp")).collect(Collectors.toList());
			ItemStack extra = ORE_EXTRAS.get(entry);
			int chance = ORE_CHANCES.get(entry);
			switch(entry.getOreType()) {
			case INGOT:
				for(ItemStack ore : ores) {
					recipes.addCrusherRecipe(ore, Utils.getOreStack("dust", entry, 2), extra, chance, 0.2F);
				}
				break;
			case GEM:
				for(ItemStack ore : ores) {
					recipes.addCrusherRecipe(ore, Utils.getOreStack("gem", entry, 2), extra, chance, 0.3F);
				}
				break;
			case DUST:
				for(ItemStack ore : ores) {
					recipes.addCrusherRecipe(ore, Utils.getOreStack("dust", entry, 6), extra, chance, 0.2F);
				}
				break;
			default:
				break;
			}
		}
	}
}
