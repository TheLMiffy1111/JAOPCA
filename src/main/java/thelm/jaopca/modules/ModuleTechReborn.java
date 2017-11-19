package thelm.jaopca.modules;

import java.security.InvalidParameterException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import reborncore.api.recipe.RecipeHandler;
import techreborn.api.ScrapboxList;
import techreborn.api.recipe.machines.ImplosionCompressorRecipe;
import techreborn.api.recipe.machines.IndustrialGrinderRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleTechReborn extends ModuleBase {

	public static final ItemEntry SMALL_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustSmall", new ModelResourceLocation("jaopca:dust_small#inventory"), ImmutableList.<String>of(
			"Almandine", "Aluminium", "Andradite", "Ashes", "Basalt", "Bauxite", "Brass", "Bronze", "Calcite", "Chromium", "Cinnabar", "Coal", "Copper",
			"Diamond", "Electrum", "Emerald", "Galena", "Gold", "Grossular", "Invar", "Iron", "Lazurite", "Lead", "Magnesium", "Manganese",
			"Nickel", "Peridot", "Phosphorous", "Platinum", "Pyrite", "Pyrope", "RedGarnet", "Ruby", "Saltpeter", "Sapphire", "Silver", "Sodalite",
			"Spessartine", "Sphalerite", "Steel", "Sulfur", "Tin", "Titanium", "Tungsten", "Uvarovite", "YellowGarnet", "Zinc", "Redstone", "Glowstone"
			)).setOreTypes(EnumOreType.values());

	public static final HashMap<IOreEntry, boolean[]> GRINDING_FLUIDS = Maps.<IOreEntry, boolean[]>newHashMap();

	@Override
	public String getName() {
		return "techreborn";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public EnumSet<EnumOreType> getOreTypes() {
		return EnumSet.allOf(EnumOreType.class);
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Copper", "Tin", "Uranium", "Coal", "Iron", "Lapis", "Redstone", "Gold", "Diamond", "Emerald", "Galena", "Lead", "Silver", "Iridium",
				"Ruby", "Sapphire", "Bauxite", "Quartz", "Pyrite", "Cinnabar", "Sphalerite", "Tungsten", "Sheldonite", "Peridot", "Sodalite", "Apatite",
				"CertusQuartz", "ChargedCertusQuartz", "Nickel", "Zinc", "Amethyst", "Topaz", "Tanzanite", "Malachite", "Pitchblende", "Aluminium",
				"Ardite", "Cobalt", "Osmium", "Teslatite", "Sulfur", "Saltpeter"
				);
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(SMALL_DUST_ENTRY);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(entry.getOreType()==EnumOreType.INGOT) {
				boolean[] data = {
						config.get(Utils.to_under_score(entry.getOreName()), "techRebornH2O", true).getBoolean(),
						config.get(Utils.to_under_score(entry.getOreName()), "techRebornNa2S2O8", true).getBoolean(),
						config.get(Utils.to_under_score(entry.getOreName()), "techRebornHg", true).getBoolean(),
				};
				GRINDING_FLUIDS.put(entry, data);
			}
			if(entry.getOreType()==EnumOreType.DUST) {
				boolean[] data = {
						config.get(Utils.to_under_score(entry.getOreName()), "techRebornH2O", true).getBoolean(),
						config.get(Utils.to_under_score(entry.getOreName()), "techRebornNa2S2O8", true).getBoolean(),
				};
				GRINDING_FLUIDS.put(entry, data);
			}
		}
	}

	@Override
	public void init() {
		FluidStack h2o = new FluidStack(FluidRegistry.WATER, 1000);
		FluidStack hg = FluidRegistry.getFluidStack("fluidmercury", 1000);
		FluidStack na2s2o8 = FluidRegistry.getFluidStack("fluidsodiumpersulfate", 1000);

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustSmall")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("dust", entry, 1), new Object[] {
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
			}));

			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("dustSmall", entry, 4), new Object[] {
					"dust"+entry.getOreName(),
			}));
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			switch(entry.getOreType()) {
			case DUST: {
				boolean[] data = GRINDING_FLUIDS.get(entry);
				if(data[0]) {
					ItemStack i0 = Utils.getOreStack("dust", entry, 5);
					ItemStack i1 = entry.hasExtra()?Utils.getOreStackExtra("dust", entry, 1):null;
					ItemStack i2 = entry.hasSecondExtra()?Utils.getOreStackSecondExtra("dustSmall", entry, 1):null;
					addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), h2o.copy(), 100, Utils.energyI(entry, 128), i0, i1, i2);
				}
				if(data[1]) {
					ItemStack i0 = Utils.getOreStack("dust", entry, 5);
					ItemStack i1 = entry.hasExtra()?Utils.getOreStackExtra("dust", entry, 3):null;
					ItemStack i2 = entry.hasSecondExtra()?Utils.getOreStackSecondExtra("dustSmall", entry, 1):null;
					addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), na2s2o8.copy(), 100, Utils.energyI(entry, 128), i0, i1, i2);
				}
				break;
			}
			case GEM: {
				ItemStack i0 = Utils.getOreStack("gem", entry, 1);
				ItemStack i1 = Utils.getOreStack("dustSmall", entry, 6);
				ItemStack i2 = entry.hasExtra()?Utils.getOreStackExtra("dustSmall", entry, 2):null;
				addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), h2o.copy(), 100, Utils.energyI(entry, 128), i0, i1, i2);
			}
			case GEM_ORELESS: {
				addImplosionCompressorRecipe(Utils.getOreStack("dust", entry, 4), Utils.getOreStack("gem", entry, 3), 12);
				addScrap(Utils.getOreStack("gem", entry, 1));
				if(Utils.doesOreNameExist("nugget"+entry.getOreName())) {
					addScrap(Utils.getOreStack("nugget", entry, 1));
				}
				break;
			}
			case INGOT: {
				boolean[] data = GRINDING_FLUIDS.get(entry);
				if(data[0]) {
					ItemStack i0 = Utils.getOreStack("dust", entry, 2);
					ItemStack i1 = entry.hasExtra()?Utils.getOreStackExtra("dustSmall", entry, 1):null;
					ItemStack i2 = entry.hasSecondExtra()?Utils.getOreStackSecondExtra("dustSmall", entry, 1):null;
					addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), h2o.copy(), 100, Utils.energyI(entry, 128), i0, i1, i2);
				}
				if(data[1]) {
					ItemStack i0 = Utils.getOreStack("dust", entry, 3);
					ItemStack i1 = entry.hasExtra()?Utils.getOreStackExtra("dustSmall", entry, 1):null;
					ItemStack i2 = entry.hasSecondExtra()?Utils.getOreStackSecondExtra("dustSmall", entry, 1):null;
					addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), na2s2o8.copy(), 100, Utils.energyI(entry, 128), i0, i1, i2);
				}
				if(data[2]) {
					ItemStack i0 = Utils.getOreStack("dust", entry, 3);
					ItemStack i1 = entry.hasExtra()?Utils.getOreStackExtra("dust", entry, 1):null;
					ItemStack i2 = entry.hasSecondExtra()?Utils.getOreStackSecondExtra("dustSmall", entry, 1):null;
					addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), hg.copy(), 100, Utils.energyI(entry, 128), i0, i1, i2);
				}
				if(Utils.doesOreNameExist("nugget"+entry.getOreName())) {
					addScrap(Utils.getOreStack("nugget", entry, 1));
				}
				break;
			}
			default:
				break;
			}
			addScrap(Utils.getOreStack("dust", entry, 1));
		}
	}

	public static void addIndustrialGrinderRecipe(ItemStack input, FluidStack fluid, int ticks, int euPerTick, ItemStack... outputs) {
		if(outputs.length == 4) {
			RecipeHandler.addRecipe(new IndustrialGrinderRecipe(input, fluid, outputs[0], outputs[1], outputs[2], outputs[3], ticks, euPerTick));
		}
		else if(outputs.length == 3) {
			RecipeHandler.addRecipe(new IndustrialGrinderRecipe(input, fluid, outputs[0], outputs[1], outputs[2], null, ticks, euPerTick));
		}
		else if(outputs.length == 2) {
			RecipeHandler.addRecipe(new IndustrialGrinderRecipe(input, fluid, outputs[0], outputs[1], null, null, ticks, euPerTick));
		}
		else if(outputs.length == 1) {
			RecipeHandler.addRecipe(new IndustrialGrinderRecipe(input, fluid, outputs[0], null, null, null, ticks, euPerTick));
		}
		else {
			throw new InvalidParameterException("Invalid industrial grinder inputs: " + outputs);
		}
	}

	public static void addImplosionCompressorRecipe(ItemStack input, ItemStack output, int darkAshes) {
		if(darkAshes < 1 || darkAshes > 64) {
			throw new InvalidParameterException("Invalid implosion compressor darkAshes input: " + darkAshes);
		}
		RecipeHandler.addRecipe(new ImplosionCompressorRecipe(input, new ItemStack(Blocks.TNT, 16), output, Utils.getOreStack("dustDarkAshes", darkAshes), 20, 32));
	}

	public static void addScrap(ItemStack stack) {
		ScrapboxList.addItemStackToList(stack);
	}
}
