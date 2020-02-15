package thelm.jaopca.compat.omegacraft;

import com.cobbs.omegacraft.Utils.Recipes.ERecipeTypes;
import com.cobbs.omegacraft.Utils.Recipes.MachineRecipe;

import thelm.jaopca.utils.MiscHelper;

public class OmegaCraftHelper {

	public static final OmegaCraftHelper INSTANCE = new OmegaCraftHelper();

	private OmegaCraftHelper() {}

	public void registerCrusherRecipe(Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondChance) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.CRUSHER, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount), helper.getItemStack(secondOutput, secondOutputCount), secondChance);
	}

	public void registerCrusherRecipe(Object input, int inputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.CRUSHER, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount));
	}

	public void registerAlloyFurnaceRecipe(Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondChance) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.ALLOY, helper.getIngredient(input), inputCount, helper.getIngredient(secondInput), secondInputCount, helper.getItemStack(output, outputCount), helper.getItemStack(secondOutput, secondOutputCount), secondChance);
	}

	public void registerAlloyFurnaceRecipe(Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.ALLOY, helper.getIngredient(input), inputCount, helper.getIngredient(secondInput), secondInputCount, helper.getItemStack(output, outputCount));
	}

	public void registerPlateFormerRecipe(Object input, int inputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.PLATE, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount));
	}

	public void registerCompactorRecipe(Object input, int inputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.COMPACTOR, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount));
	}

	public void registerHydratorRecipe(Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondChance) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.HYDRO, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount), helper.getItemStack(secondOutput, secondOutputCount), secondChance);
	}

	public void registerHydratorRecipe(Object input, int inputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.HYDRO, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount));
	}

	public void registerCombinerRecipe(Object input, int inputCount, Object secondInput, int secondInputCount, Object thirdInput, int thirdInputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondChance) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.COMBINER, helper.getIngredient(input), inputCount, helper.getIngredient(secondInput), secondInputCount, helper.getIngredient(thirdInput), thirdInputCount, helper.getItemStack(output, outputCount), helper.getItemStack(secondOutput, secondOutputCount), secondChance);
	}

	public void registerCombinerRecipe(Object input, int inputCount, Object secondInput, int secondInputCount, Object thirdInput, int thirdInputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.COMBINER, helper.getIngredient(input), inputCount, helper.getIngredient(secondInput), secondInputCount, helper.getIngredient(thirdInput), thirdInputCount, helper.getItemStack(output, outputCount));
	}

	public void registerDarkFabricatorRecipe(Object input, int inputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.DARK_FAB, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount));
	}

	public void registerOreWasherRecipe(Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondChance) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.ORE_WASHER, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount), helper.getItemStack(secondOutput, secondOutputCount), secondChance);
	}

	public void registerOreWasherRecipe(Object input, int inputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.ORE_WASHER, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount));
	}

	public void registerAlloySmelterRecipe(Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondChance) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.ALLOY_SMELTER, helper.getIngredient(input), inputCount, helper.getIngredient(secondInput), secondInputCount, helper.getItemStack(output, outputCount), helper.getItemStack(secondOutput, secondOutputCount), secondChance);
	}

	public void registerAlloySmelterRecipe(Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.ALLOY_SMELTER, helper.getIngredient(input), inputCount, helper.getIngredient(secondInput), secondInputCount, helper.getItemStack(output, outputCount));
	}

	public void registerBlockCompactorRecipe(Object input, int inputCount, Object output, int outputCount) {
		MiscHelper helper = MiscHelper.INSTANCE;
		new MachineRecipe(ERecipeTypes.BLOCK_COMPACT, helper.getIngredient(input), inputCount, helper.getItemStack(output, outputCount));
	}
}
