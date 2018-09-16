package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper.GrinderOutput;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleStaticPower extends ModuleBase {

	public static final ArrayList<String> FULL_BLACKLIST = Lists.<String>newArrayList(
			"Copper", "Silver", "Tin", "Lead", "Platinum", "Nickel", "Aluminium", "Iron", "Gold", /*"Ruby",
			"Sapphire",*/ "Static", "Energized", "Lumum", "InertInfusion", "RestoneAlloy", "Coal", "Bronze",
			"Redstone", "Sulfur", "Saltpeter", "Potash", "Bitumen", "Salt", /*"Steel", "Electrum", "Invar",*/
			/*"Peridot",*/ "Prismarine"
			);
	public static final ArrayList<String> ORE_BLACKLIST = Lists.<String>newArrayList(
			"Aluminium", "Manganese", "Mithril", "Zinc", "Adamantine", "Alduorite", "AstralSilver",
			"Atlarus", "Carmot", "Ceruclase", "DeepIron", "Eximite", "Ignatius", "Infuscolium", "Kalendrite",
			"Lemurite", "Magnesium", "Meutoite", "Midasium", "Orichalum", "Oureclase", "Prometheum", "Rubracium",
			"Sanguinite", "ShadowIron", "Vulcanite", "Vyroxeres", "Diamond", "Emerald", "Quartz", "Lapis"
			);
	public static final ArrayList<String> DUST_BLACKLIST = Lists.<String>newArrayList(
			/*"Uranium"*/
			);

	@Override
	public String getName() {
		return "staticpower";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<String> getOreBlacklist() {
		return FULL_BLACKLIST;
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return EnumSet.<EnumOreType>allOf(EnumOreType.class);
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			switch(entry.getOreType()) {
			case INGOT:
				if(!ORE_BLACKLIST.contains(entry.getOreName())) {
					ArrayList<Pair<ItemStack, Float>> list = Lists.<Pair<ItemStack, Float>>newArrayList();
					list.add(Pair.of(Utils.getOreStack("dust", entry, 2), 1F));
					if(entry.hasExtra()) {
						list.add(Pair.of(Utils.getOreStackExtra("dust", entry, 1), 0.05F));
					}
					for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
						addGrinderRecipe(ore, list);
					}
				}
			case INGOT_ORELESS:
				if(!DUST_BLACKLIST.contains(entry.getOreName())) {
					for(ItemStack ore : Utils.getOres("ingot"+entry.getOreName())) {
						addGrinderRecipe(ore, Utils.getOreStackExtra("dust", entry, 1), 1F);
					}
				}
				break;
			case GEM:
				if(!ORE_BLACKLIST.contains(entry.getOreName())) {
					for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
						addGrinderRecipe(ore, Utils.getOreStack("gem", entry, 3), 1F);
					}
				}
			case GEM_ORELESS:
				if(!DUST_BLACKLIST.contains(entry.getOreName())) {
					for(ItemStack ore : Utils.getOres("gem"+entry.getOreName())) {
						addGrinderRecipe(ore, Utils.getOreStackExtra("dust", entry, 1), 1F);
					}
				}
				break;
			case DUST:
				if(!ORE_BLACKLIST.contains(entry.getOreName())) {
					for(ItemStack ore : Utils.getOres("ore"+entry.getOreName())) {
						addGrinderRecipe(ore, Utils.getOreStack("dust", entry, 4), 1F);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	public static void addGrinderRecipe(ItemStack input, List<Pair<ItemStack, Float>> outputs) {
		RegisterHelper.registerGrinderRecipe(input, Lists.transform(outputs, pair->new GrinderOutput(pair.getLeft(), pair.getRight())).toArray(new GrinderOutput[outputs.size()]));
	}

	public static void addGrinderRecipe(ItemStack input, ItemStack output, float chance) {
		RegisterHelper.registerGrinderRecipe(input, new GrinderOutput(output, chance));
	}
}
