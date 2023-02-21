package thelm.jaopca.compat.mekanism;

import java.util.function.Supplier;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import thelm.jaopca.compat.mekanism.api.gases.IGasProvider;
import thelm.jaopca.compat.mekanism.recipes.ChemicalCrystallizerRecipeAction;
import thelm.jaopca.compat.mekanism.recipes.ChemicalDissolutionChamberRecipeAction;
import thelm.jaopca.compat.mekanism.recipes.ChemicalInjectionChamberRecipeAction;
import thelm.jaopca.compat.mekanism.recipes.ChemicalWasherRecipeAction;
import thelm.jaopca.compat.mekanism.recipes.CombinerRecipeAction;
import thelm.jaopca.compat.mekanism.recipes.CrusherRecipeAction;
import thelm.jaopca.compat.mekanism.recipes.EnrichmentChamberRecipeAction;
import thelm.jaopca.compat.mekanism.recipes.PurificationChamberRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class MekanismHelper {

	public static final MekanismHelper INSTANCE = new MekanismHelper();

	private MekanismHelper() {}

	public GasStack getGasStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getGasStack(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof GasStack) {
			return resizeGasStack((GasStack)obj, amount);
		}
		else if(obj instanceof Gas) {
			return new GasStack((Gas)obj, amount);
		}
		else if(obj instanceof IGasProvider) {
			return new GasStack(((IGasProvider)obj).asGas(), amount);
		}
		else if(obj instanceof String) {
			return new GasStack(GasRegistry.getGas((String)obj), amount);
		}
		return null;
	}

	public GasStack resizeGasStack(GasStack stack, int amount) {
		if(stack != null) {
			GasStack ret = stack.copy();
			ret.amount = amount;
			return ret;
		}
		return null;
	}

	public boolean registerCrusherRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrusherRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerEnrichmentChamberRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new EnrichmentChamberRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerCombinerRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CombinerRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerChemicalWasherRecipe(String key, Object input, int inputAmount, Object output, int outputAmount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalWasherRecipeAction(key, input, inputAmount, output, outputAmount));
	}

	public boolean registerChemicalCrystallizerRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalCrystallizerRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerChemicalDissolutionChamberRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalDissolutionChamberRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerPurificationChamberRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PurificationChamberRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerChemicalInjectionChamberRecipe(String key, Object input, int inputCount, Object gasInput, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalInjectionChamberRecipeAction(key, input, inputCount, gasInput, output, outputCount));
	}
}
