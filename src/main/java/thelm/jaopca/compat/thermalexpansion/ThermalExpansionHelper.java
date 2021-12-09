package thelm.jaopca.compat.thermalexpansion;

import java.util.Arrays;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import cofh.lib.fluid.FluidIngredient;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.compat.thermalexpansion.recipes.ChillerRecipeSupplier;
import thelm.jaopca.compat.thermalexpansion.recipes.PressRecipeSupplier;
import thelm.jaopca.compat.thermalexpansion.recipes.PulverizerRecipeSupplier;
import thelm.jaopca.compat.thermalexpansion.recipes.SmelterRecipeSupplier;
import thelm.jaopca.tags.EmptyNamedTag;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class ThermalExpansionHelper {

	public static final ThermalExpansionHelper INSTANCE = new ThermalExpansionHelper();

	private ThermalExpansionHelper() {}

	public Ingredient getCountedIngredient(Object obj, int count) {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(obj);
		for(ItemStack stack : ing.getMatchingStacks()) {
			stack.setCount(count);
		}
		return ing;
	}

	public FluidIngredient getFluidIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidIngredient) {
			return (FluidIngredient)obj;
		}
		else if(obj instanceof String) {
			return FluidIngredient.of(getFluidTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return FluidIngredient.of(getFluidTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof ITag<?>) {
			return FluidIngredient.of((ITag<Fluid>)obj, amount);
		}
		else if(obj instanceof FluidStack) {
			return FluidIngredient.of((FluidStack)obj);
		}
		else if(obj instanceof FluidStack[]) {
			return FluidIngredient.of((FluidStack[])obj);
		}
		else if(obj instanceof Fluid) {
			return FluidIngredient.of(new FluidStack((Fluid)obj, amount));
		}
		else if(obj instanceof Fluid[]) {
			return FluidIngredient.of(Arrays.stream((Fluid[])obj).map(g->new FluidStack(g, amount)).toArray(FluidStack[]::new));
		}
		else if(obj instanceof JsonElement) {
			return FluidIngredient.fromJson((JsonElement)obj);
		}
		return FluidIngredient.of();
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, int inputCount, Object[] output, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSupplier(key, input, inputCount, output, energy, experience));
	}

	public boolean registerSmelterRecipe(ResourceLocation key, Object[] input, Object[] output, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SmelterRecipeSupplier(key, input, output, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, fluidOutput, fluidOutputAmount, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSupplier(key, input, inputCount, itemOutput, itemOutputCount, energy, experience));
	}

	public boolean registerChillerRecipe(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object itemInput, int itemInputCount, Object output, int outputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChillerRecipeSupplier(key, fluidInput, fluidInputAmount, itemInput, itemInputCount, output, outputCount, energy, experience));
	}

	public ITag<Fluid> getFluidTag(ResourceLocation location) {
		ITag<Fluid> tag = TagCollectionManager.getManager().getFluidTags().get(location);
		return tag != null ? tag : new EmptyNamedTag<>(location);
	}
}
