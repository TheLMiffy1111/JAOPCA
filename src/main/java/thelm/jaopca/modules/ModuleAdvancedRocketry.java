package thelm.jaopca.modules;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;
import zmaster587.advancedRocketry.block.BlockSmallPlatePress;
import zmaster587.libVulpes.recipe.RecipesMachine;

public class ModuleAdvancedRocketry extends ModuleBase {

	public static final ArrayList<String> BLACKLIST = Lists.<String>newArrayList(
			"Dilithium", "Iron", "Gold", "Copper", "Tin", "Aluminium", "Iridium", "Rutile", "Titanium", "Silicon"
			);

	@Override
	public String getName() {
		return "advancedrocketry";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<String> getOreBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			addPresserRecipe("ore"+entry.getOreName(), Utils.getOreStack("dust", entry, 2), 0, 0);
		}
	}

	public static void addPresserRecipe(List<Object> input, List<Object> output, int time, int power) {
		RecipesMachine.getInstance().addRecipe(BlockSmallPlatePress.class, output, time, power, input);
	}

	public static void addPresserRecipe(Object[] input, Object[] output, int time, int power) {
		RecipesMachine.getInstance().addRecipe(BlockSmallPlatePress.class, output, time, power, input);
	}

	public static void addPresserRecipe(Object input, Object output, int time, int power) {
		RecipesMachine.getInstance().addRecipe(BlockSmallPlatePress.class, output, time, power, input);
	}
}
