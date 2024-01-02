package thelm.jaopca.compat.electrodynamics;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import electrodynamics.common.recipe.recipeutils.CountableIngredient;
import electrodynamics.common.recipe.recipeutils.FluidIngredient;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.compat.electrodynamics.recipes.ChemicalCrystallizerRecipeSupplier;
import thelm.jaopca.compat.electrodynamics.recipes.LatheRecipeSupplier;
import thelm.jaopca.compat.electrodynamics.recipes.MineralCrusherRecipeSupplier;
import thelm.jaopca.compat.electrodynamics.recipes.MineralGrinderRecipeSupplier;
import thelm.jaopca.compat.electrodynamics.recipes.MineralWasherRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class ElectrodynamicsHelper {

	public static final ElectrodynamicsHelper INSTANCE = new ElectrodynamicsHelper();

	private ElectrodynamicsHelper() {}

	public CountableIngredient getCountableIngredient(Object obj, int count) {
		return new CountableIngredient(MiscHelper.INSTANCE.getIngredient(obj), count) {};
	}

	public FluidIngredient getFluidIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidIngredient) {
			return (FluidIngredient)obj;
		}
		else if(obj instanceof String) {
			return new FluidIngredient(MiscHelper.INSTANCE.getFluidTag(new ResourceLocation((String)obj)).getValues().stream().map(f->new FluidStack(f, amount)).collect(Collectors.toList()));
		}
		else if(obj instanceof ResourceLocation) {
			return new FluidIngredient(MiscHelper.INSTANCE.getFluidTag((ResourceLocation)obj).getValues().stream().map(f->new FluidStack(f, amount)).collect(Collectors.toList()));
		}
		else if(obj instanceof FluidStack) {
			return new FluidIngredient((FluidStack)obj);
		}
		else if(obj instanceof FluidStack[]) {
			return new FluidIngredient(Arrays.asList((FluidStack[])obj));
		}
		else if(obj instanceof Fluid) {
			return new FluidIngredient(new FluidStack((Fluid)obj, amount));
		}
		else if(obj instanceof Fluid[]) {
			return new FluidIngredient(Arrays.stream((Fluid[])obj).map(g->new FluidStack(g, amount)).collect(Collectors.toList()));
		}
		else if(obj instanceof JsonObject) {
			return FluidIngredient.deserialize((JsonObject)obj);
		}
		return new FluidIngredient(Collections.emptyList());
	}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSupplier(key, input, inputCount, output, outputCount));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSupplier(key, input, inputCount, output, outputCount));
	}

	public boolean registerLatheRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSupplier(key, input, inputCount, output, outputCount));
	}

	public boolean registerChemicalCrystallizerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalCrystallizerRecipeSupplier(key, input, inputAmount, output, outputCount));
	}

	public boolean registerMineralWasherRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralWasherRecipeSupplier(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputAmount));
	}
}
