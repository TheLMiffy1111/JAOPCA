package thelm.jaopca.compat.ic2;

import java.util.function.Supplier;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.IRecipeInputFactory;
import ic2.api.recipe.Recipes;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
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
		IRecipeInputFactory inputFactory = Recipes.inputFactory;
		if(obj instanceof Supplier<?>) {
			return getRecipeInput(((Supplier<?>)obj).get(), amount);
		}
		if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return inputFactory.forOreDict((String)obj, amount);
			}
		}
		if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			if(!stack.isEmpty()) {
				return inputFactory.forStack(stack, amount);
			}
		}
		if(obj instanceof Item) {
			if(obj != Items.AIR) {
				return inputFactory.forStack(new ItemStack((Item)obj, amount));
			}
		}
		if(obj instanceof Block) {
			if(obj != Blocks.AIR) {
				return inputFactory.forStack(new ItemStack((Block)obj, amount));
			}
		}
		if(obj instanceof IItemProvider) {
			Item item = ((IItemProvider)obj).asItem();
			if(item != Items.AIR) {
				return inputFactory.forStack(new ItemStack(item, amount));
			}
		}
		if(obj instanceof FluidStack) {
			return inputFactory.forFluidContainer(((FluidStack)obj).getFluid(), amount);
		}
		if(obj instanceof Fluid) {
			return inputFactory.forFluidContainer((Fluid)obj, amount);
		}
		if(obj instanceof Ingredient) {
			return inputFactory.forIngredient((Ingredient)obj);
		}
		if(obj instanceof IRecipeInput) {
			return (IRecipeInput)obj;
		}
		return null;
	}

	public boolean registerMaceratorRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MaceratorRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, int inputCount, int minHeat, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeAction(key, input, inputCount, minHeat, output));
	}

	public boolean registerOreWashingRecipe(ResourceLocation key, Object input, int inputCount, int waterAmount, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreWashingRecipeAction(key, input, inputCount, waterAmount, output));
	}

	public boolean registerCompressorRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressorRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerMetalFormerRollingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MetalFormerRollingRecipeAction(key, input, inputCount, output, outputCount));
	}

	public boolean registerBlockCutterRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int hardness) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlockCutterRecipeAction(key, input, inputCount, output, outputCount, hardness));
	}
}
