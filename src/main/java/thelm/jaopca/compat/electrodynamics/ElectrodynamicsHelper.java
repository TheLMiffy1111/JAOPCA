package thelm.jaopca.compat.electrodynamics;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidProvider;
import thelm.jaopca.compat.electrodynamics.recipes.ChemicalCrystallizerRecipeSupplier;
import thelm.jaopca.compat.electrodynamics.recipes.LatheRecipeSupplier;
import thelm.jaopca.compat.electrodynamics.recipes.MineralCrusherRecipeSupplier;
import thelm.jaopca.compat.electrodynamics.recipes.MineralGrinderRecipeSupplier;
import thelm.jaopca.compat.electrodynamics.recipes.MineralWasherRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;

public class ElectrodynamicsHelper {

	public static final ElectrodynamicsHelper INSTANCE = new ElectrodynamicsHelper();

	private ElectrodynamicsHelper() {}

	public CountableIngredient getCountableIngredient(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getCountableIngredient(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof CountableIngredient) {
			return (CountableIngredient)obj;
		}
		else if(obj instanceof String) {
			return new CountableIngredient(ItemTags.createOptional(new ResourceLocation((String)obj)), count);
		}
		else if(obj instanceof ResourceLocation) {
			return new CountableIngredient(ItemTags.createOptional((ResourceLocation)obj), count);
		}
		else if(obj instanceof Item) {
			if(obj != Items.AIR) {
				return new CountableIngredient(new ItemStack((Item)obj, count));
			}
		}
		else if(obj instanceof IItemProvider) {
			Item item = ((IItemProvider)obj).asItem();
			if(item != Items.AIR) {
				return new CountableIngredient(new ItemStack(item, count));
			}
		}
		else if(obj instanceof JsonObject) {
			return CountableIngredient.CODEC.parse(JsonOps.INSTANCE, (JsonObject)obj).get().left().orElse(new CountableIngredient(ItemStack.EMPTY));
		}
		return new CountableIngredient(ItemStack.EMPTY);
	}

	public FluidIngredient getFluidIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidIngredient) {
			return (FluidIngredient)obj;
		}
		else if(obj instanceof String) {
			return new FluidIngredient(FluidTags.createOptional(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return new FluidIngredient(FluidTags.createOptional((ResourceLocation)obj), amount);
		}
		else if(obj instanceof FluidStack) {
			FluidStack stack = (FluidStack)obj;
			if(!stack.isEmpty()) {
				return new FluidIngredient(stack);
			}
		}
		else if(obj instanceof FluidStack[]) {
			return new FluidIngredient(Arrays.stream((FluidStack[])obj).filter(s->!s.isEmpty()).collect(Collectors.toList()));
		}
		else if(obj instanceof Fluid) {
			if(obj != Fluids.EMPTY) {
				return new FluidIngredient(new FluidStack((Fluid)obj, amount));
			}
		}
		else if(obj instanceof Fluid[]) {
			return new FluidIngredient(Arrays.stream((Fluid[])obj).filter(f->f != Fluids.EMPTY).map(f->new FluidStack(f, amount)).collect(Collectors.toList()));
		}
		else if(obj instanceof IFluidProvider) {
			Fluid fluid = ((IFluidProvider)obj).asFluid();
			if(fluid != Fluids.EMPTY) {
				return new FluidIngredient(new FluidStack(fluid, amount));
			}
		}
		else if(obj instanceof IFluidProvider[]) {
			return new FluidIngredient(Arrays.stream((IFluidProvider[])obj).map(IFluidProvider::asFluid).filter(f->f != Fluids.EMPTY).map(f->new FluidStack(f, amount)).collect(Collectors.toList()));
		}
		else if(obj instanceof JsonObject) {
			return FluidIngredient.CODEC.parse(JsonOps.INSTANCE, (JsonObject)obj).get().left().orElse(new FluidIngredient(Collections.emptyList()));
		}
		return new FluidIngredient(Collections.emptyList());
	}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSupplier(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSupplier(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSupplier(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSupplier(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerLatheRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSupplier(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerLatheRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSupplier(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerChemicalCrystallizerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalCrystallizerRecipeSupplier(key, input, inputAmount, output, outputCount, experience, time, energy));
	}

	public boolean registerMineralWasherRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralWasherRecipeSupplier(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputAmount, experience, time, energy));
	}
}
