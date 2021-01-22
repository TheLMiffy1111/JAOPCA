package thelm.jaopca.compat.bloodmagic;

import java.util.Arrays;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.compat.bloodmagic.recipes.ARCRecipeSupplier;
import thelm.jaopca.compat.bloodmagic.recipes.AlchemyTableRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
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
			return FluidStackIngredient.from(getFluidTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return FluidStackIngredient.from(getFluidTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof ITag<?>) {
			return FluidStackIngredient.from((ITag<Fluid>)obj, amount);
		}
		else if(obj instanceof FluidStack) {
			return FluidStackIngredient.from((FluidStack)obj);
		}
		else if(obj instanceof FluidStack[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((FluidStack[])obj).map(FluidStackIngredient::from).toArray(FluidStackIngredient[]::new));
		}
		else if(obj instanceof Fluid) {
			return FluidStackIngredient.from((Fluid)obj, amount);
		}
		else if(obj instanceof Fluid[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((Fluid[])obj).map(g->FluidStackIngredient.from(g, amount)).toArray(FluidStackIngredient[]::new));
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

	public ITag<Fluid> getFluidTag(ResourceLocation location) {
		ITag<Fluid> tag = TagCollectionManager.getManager().getFluidTags().get(location);
		return tag != null ? tag : Tag.getEmptyTag();
	}
}
