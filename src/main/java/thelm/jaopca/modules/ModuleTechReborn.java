package thelm.jaopca.modules;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import reborncore.api.recipe.RecipeHandler;
import techreborn.api.ScrapboxList;
import techreborn.api.recipe.machines.IndustrialGrinderRecipe;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleTechReborn extends ModuleBase {

	public static final ItemEntry SMALL_DUST_ENTRY = new ItemEntry(EnumEntryType.ITEM, "dustSmall", new ModelResourceLocation("jaopca:dust_small#inventory"), ImmutableList.<String>of(
			"Aluminum", "Copper", "Gold", "Iron", "Lead", "Magnesium", "Manganese", "Nickel", "Platinum", "Silver", "Steel", "Tin", "Titanium", "Tungsten", "Zinc"
			));

	public static final ArrayList<String> COMPAT_ORE_BLACKLIST = Lists.<String>newArrayList(
			"Osmium", "Ardite", "Cobalt", "Iridium"
			);

	public static final HashMap<String, boolean[]> GRINDING_FLUIDS = Maps.<String, boolean[]>newHashMap();

	@Override
	public String getName() {
		return "techreborn";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Aluminum", "Copper", "Gold", "Iron", "Lead", "Magnesium", "Manganese", "Nickel", "Platinum", "Silver", "Steel", "Tin", "Titanium", "Tungsten", "Zinc"
				);
	}

	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList(SMALL_DUST_ENTRY);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			boolean[] data = {
					config.get(Utils.to_under_score(entry.getOreName()), "techRebornH2O", true).getBoolean(),
					config.get(Utils.to_under_score(entry.getOreName()), "techRebornNa2S2O8", true).getBoolean(),
					config.get(Utils.to_under_score(entry.getOreName()), "techRebornHg", true).getBoolean(),
			};
			GRINDING_FLUIDS.put(entry.getOreName(), data);
		}
	}

	@Override
	public void init() {
		FluidStack h2o = new FluidStack(FluidRegistry.WATER, 1000);
		FluidStack hg = new FluidStack(FluidRegistry.getFluid("fluidmercury"), 1000);
		FluidStack na2s2o8 = new FluidStack(FluidRegistry.getFluid("fluidsodiumpersulfate"), 1000);

		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dustSmall")) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(Utils.getOreStack("dust", entry, 1), new Object[] {
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
					"dustSmall"+entry.getOreName(),
			}));
		}

		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			if(!COMPAT_ORE_BLACKLIST.contains(entry.getOreName())) {
				boolean[] data = GRINDING_FLUIDS.get(entry.getOreName());
				if(data[0]) {
					addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), h2o, 100, Utils.energyI(entry, 128), Utils.getOreStack("dust", entry, 2), Utils.getOreStackExtra("dustSmall", entry, 1));
				}
				if(data[1]) {
					addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), na2s2o8, 100, Utils.energyI(entry, 128), Utils.getOreStack("dust", entry, 3), Utils.getOreStackExtra("dustSmall", entry, 1));
				}
				if(data[2]) {
					addIndustrialGrinderRecipe(Utils.getOreStack("ore", entry, 1), hg, 100, Utils.energyI(entry, 128), Utils.getOreStack("dust", entry, 3), Utils.getOreStackExtra("dust", entry, 1));
				}
			}
			addScrap(Utils.getOreStack("dust", entry, 1));
			if(Utils.doesOreNameExist("nugget"+entry.getOreName())) {
				addScrap(Utils.getOreStack("nugget", entry, 1));
			}
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

	public static void addScrap(ItemStack stack) {
		ScrapboxList.addItemStackToList(stack);
	}
}
