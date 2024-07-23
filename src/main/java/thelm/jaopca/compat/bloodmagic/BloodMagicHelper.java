package thelm.jaopca.compat.bloodmagic;

import java.util.Arrays;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidProvider;
import thelm.jaopca.compat.bloodmagic.recipes.ARCRecipeSupplier;
import thelm.jaopca.compat.bloodmagic.recipes.AlchemyTableRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;
import wayoftime.bloodmagic.recipe.helper.FluidStackIngredient;

public class BloodMagicHelper {

	public static final BloodMagicHelper INSTANCE = new BloodMagicHelper();

	private BloodMagicHelper() {}

	public FluidStackIngredient getFluidStackIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidStackIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidStackIngredient) {
			return (FluidStackIngredient)obj;
		}
		else if(obj instanceof String) {
			return FluidStackIngredient.from(MiscHelper.INSTANCE.getFluidTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return FluidStackIngredient.from(MiscHelper.INSTANCE.getFluidTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof ITag<?>) {
			return FluidStackIngredient.from((ITag<Fluid>)obj, amount);
		}
		else if(obj instanceof FluidStack) {
			FluidStack stack = (FluidStack)obj;
			if(!stack.isEmpty()) {
				return FluidStackIngredient.from(stack);
			}
		}
		else if(obj instanceof FluidStack[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((FluidStack[])obj).filter(s->!s.isEmpty()).map(FluidStackIngredient::from).toArray(FluidStackIngredient[]::new));
		}
		else if(obj instanceof Fluid) {
			if(obj != Fluids.EMPTY) {
				return FluidStackIngredient.from((Fluid)obj, amount);
			}
		}
		else if(obj instanceof Fluid[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((Fluid[])obj).filter(f->f != Fluids.EMPTY).map(g->FluidStackIngredient.from(g, amount)).toArray(FluidStackIngredient[]::new));
		}
		else if(obj instanceof IFluidProvider) {
			Fluid fluid = ((IFluidProvider)obj).asFluid();
			if(fluid != Fluids.EMPTY) {
				return FluidStackIngredient.from(fluid, amount);
			}
		}
		else if(obj instanceof IFluidProvider[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((IFluidProvider[])obj).map(IFluidProvider::asFluid).filter(f->f != Fluids.EMPTY).map(g->FluidStackIngredient.from(g, amount)).toArray(FluidStackIngredient[]::new));
		}
		else if(obj instanceof JsonElement) {
			return FluidStackIngredient.deserialize((JsonElement)obj);
		}
		return null;
	}

	public boolean registerAlchemyTableRecipe(ResourceLocation key, Object[] input, Object output, int count, int cost, int time, int minTier) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlchemyTableRecipeSupplier(key, input, output, count, cost, time, minTier));
	}

	public boolean registerARCRecipe(ResourceLocation key, Object input, Object tool, Object fluidInput, int fluidInputAmount, Object[] output, Object fluidOutput, int fluidOutputAmount, boolean consumeInput) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ARCRecipeSupplier(key, input, tool, fluidInput, fluidInputAmount, output, fluidOutput, fluidOutputAmount, consumeInput));
	}

	public boolean registerARCRecipe(ResourceLocation key, Object input, Object tool, Object[] output, boolean consumeInput) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ARCRecipeSupplier(key, input, tool, output, consumeInput));
	}
}
