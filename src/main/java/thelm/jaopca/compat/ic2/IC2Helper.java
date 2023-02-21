package thelm.jaopca.compat.ic2;

import java.util.function.Supplier;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputFluidContainer;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.ic2.recipes.BlockCutterRecipeAction;
import thelm.jaopca.compat.ic2.recipes.CentrifugeRecipeAction;
import thelm.jaopca.compat.ic2.recipes.CompressorRecipeAction;
import thelm.jaopca.compat.ic2.recipes.MaceratorRecipeAction;
import thelm.jaopca.compat.ic2.recipes.MetalFormerRollingRecipeAction;
import thelm.jaopca.compat.ic2.recipes.OreWashingRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class IC2Helper {

	public static final IC2Helper INSTANCE = new IC2Helper();

	private IC2Helper() {}

	public IRecipeInput getRecipeInput(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getRecipeInput(((Supplier<?>)obj).get(), amount);
		}
		if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return new RecipeInputOreDict((String)obj, amount);
			}
		}
		if(obj instanceof ItemStack) {
			return new RecipeInputItemStack((ItemStack)obj, amount);
		}
		if(obj instanceof Item) {
			return new RecipeInputItemStack(new ItemStack((Item)obj, amount, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof Block) {
			return new RecipeInputItemStack(new ItemStack((Block)obj, amount, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof IItemProvider) {
			return new RecipeInputItemStack(new ItemStack(((IItemProvider)obj).asItem(), amount, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof FluidStack) {
			return new RecipeInputFluidContainer(((FluidStack)obj).getFluid(), amount);
		}
		if(obj instanceof Fluid) {
			return new RecipeInputFluidContainer((Fluid)obj, amount);
		}
		if(obj instanceof IRecipeInput) {
			return (IRecipeInput)obj;
		}
		return null;
	}

	public boolean registerMaceratorRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MaceratorRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerCentrifugeRecipe(String key, Object input, int inputCount, int minHeat, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeAction(key, input, inputCount, minHeat, output));
	}

	public boolean registerOreWashingRecipe(String key, Object input, int inputCount, int waterAmount, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreWashingRecipeAction(key, input, inputCount, waterAmount, output));
	}

	public boolean registerCompressorRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerMetalFormerRollingRecipe(String key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MetalFormerRollingRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerBlockCutterRecipe(String key, Object input, int inputCount, Object output, int outputCount, int hardness) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlockCutterRecipeAction(key, input, inputCount, output, outputCount, hardness));
	}
}
