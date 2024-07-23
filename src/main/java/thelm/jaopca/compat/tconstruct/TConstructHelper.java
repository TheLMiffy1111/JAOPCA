package thelm.jaopca.compat.tconstruct;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import com.google.gson.JsonElement;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import thelm.jaopca.api.fluids.IFluidProvider;
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
			FluidStack stack = (FluidStack)obj;
			if(!stack.isEmpty()) {
				return FluidIngredient.of(stack);
			}
		}
		else if(obj instanceof FluidStack[]) {
			return FluidIngredient.of(Arrays.stream((FluidStack[])obj).filter(s->!s.isEmpty()).map(FluidIngredient::of).toArray(FluidIngredient[]::new));
		}
		else if(obj instanceof Fluid) {
			if(obj != Fluids.EMPTY) {
				return FluidIngredient.of((Fluid)obj, amount);
			}
		}
		else if(obj instanceof Fluid[]) {
			return FluidIngredient.of(Arrays.stream((Fluid[])obj).filter(f->f != Fluids.EMPTY).map(f->FluidIngredient.of(f, amount)).toArray(FluidIngredient[]::new));
		}
		else if(obj instanceof IFluidProvider) {
			Fluid fluid = ((IFluidProvider)obj).asFluid();
			if(fluid != Fluids.EMPTY) {
				return FluidIngredient.of(fluid, amount);
			}
		}
		else if(obj instanceof IFluidProvider[]) {
			return FluidIngredient.of(Arrays.stream((IFluidProvider[])obj).map(IFluidProvider::asFluid).filter(f->f != Fluids.EMPTY).map(f->FluidIngredient.of(f, amount)).toArray(FluidIngredient[]::new));
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
			ItemStack stack = (ItemStack)obj;
			if(!stack.isEmpty()) {
				return ItemOutput.fromStack((ItemStack)obj);
			}
		}
		else if(obj instanceof IItemProvider) {
			Item item = ((IItemProvider)obj).asItem();
			if(item != Items.AIR) {
				return ItemOutput.fromStack(new ItemStack(item, count));
			}
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
