package thelm.jaopca.compat.tconstruct;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.google.gson.JsonElement;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import thelm.jaopca.compat.tconstruct.recipes.CastingBasinRecipeSupplier;
import thelm.jaopca.compat.tconstruct.recipes.CastingTableRecipeSupplier;
import thelm.jaopca.compat.tconstruct.recipes.MeltingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class TConstructHelper {

	public static final TConstructHelper INSTANCE = new TConstructHelper();

	private TConstructHelper() {}

	public FluidIngredient getFluidIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidIngredient) {
			return (FluidIngredient)obj;
		}
		else if(obj instanceof String) {
			return FluidIngredient.of(MiscHelper.INSTANCE.getFluidTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return FluidIngredient.of(MiscHelper.INSTANCE.getFluidTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof ITag<?>) {
			return FluidIngredient.of((ITag<Fluid>)obj, amount);
		}
		else if(obj instanceof FluidStack) {
			return FluidIngredient.of((FluidStack)obj);
		}
		else if(obj instanceof FluidStack[]) {
			return FluidIngredient.of(Arrays.stream((FluidStack[])obj).map(FluidIngredient::of).toArray(FluidIngredient[]::new));
		}
		else if(obj instanceof Fluid) {
			return FluidIngredient.of((Fluid)obj, amount);
		}
		else if(obj instanceof Fluid[]) {
			return FluidIngredient.of(Arrays.stream((Fluid[])obj).map(g->FluidIngredient.of(g, amount)).toArray(FluidIngredient[]::new));
		}
		else if(obj instanceof JsonElement) {
			return FluidIngredient.deserialize((JsonElement)obj, "ingredient");
		}
		return FluidIngredient.EMPTY;
	}

	public ItemOutput getItemOutput(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getItemOutput(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof ItemOutput) {
			return ((ItemOutput)obj);
		}
		else if(obj instanceof ItemStack) {
			return ItemOutput.fromStack((ItemStack)obj);
		}
		else if(obj instanceof IItemProvider) {
			return ItemOutput.fromStack(new ItemStack((IItemProvider)obj, count));
		}
		else if(obj instanceof String) {
			return ItemOutput.fromTag(MiscHelper.INSTANCE.getItemTag(new ResourceLocation((String)obj)), count);
		}
		else if(obj instanceof ResourceLocation) {
			return ItemOutput.fromTag(MiscHelper.INSTANCE.getItemTag((ResourceLocation)obj), count);
		}
		else if(obj instanceof ITag<?>) {
			return ItemOutput.fromTag((ITag<Item>)obj, count);
		}
		return ItemOutput.fromStack(ItemStack.EMPTY);
	}

	public boolean registerMeltingRecipe(ResourceLocation key, String group, Object input, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, boolean isOre, Object... byproducts) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSupplier(key, group, input, output, outputAmount, temperature, time, isOre, byproducts));
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, boolean isOre, Object... byproducts) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSupplier(key, input, output, outputAmount, temperature, time, isOre, byproducts));
	}

	public boolean registerCastingTableRecipe(ResourceLocation key, String group, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingTableRecipeSupplier(key, group, cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots));
	}

	public boolean registerCastingTableRecipe(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingTableRecipeSupplier(key, cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots));
	}

	public boolean registerCastingBasinRecipe(ResourceLocation key, String group, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingBasinRecipeSupplier(key, group, cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots));
	}

	public boolean registerCastingBasinRecipe(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingBasinRecipeSupplier(key, cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots));
	}
}
