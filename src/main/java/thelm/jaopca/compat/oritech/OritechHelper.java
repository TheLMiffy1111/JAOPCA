package thelm.jaopca.compat.oritech;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import rearth.oritech.util.FluidIngredient;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.oritech.recipes.AtomicForgeRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.CentrifugeFluidRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.CentrifugeRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.FoundryRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.GrinderRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.PulverizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class OritechHelper {

	public static final OritechHelper INSTANCE = new OritechHelper();

	private OritechHelper() {}

	public FluidIngredient getFluidIngredient(Object obj, long amount) {
		FluidIngredient ing = FluidIngredient.EMPTY;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		switch(obj) {
		case Supplier<?> supplier -> {
			ing = getFluidIngredient(supplier.get(), amount);
		}
		case FluidIngredient fluidIng -> {
			ing = fluidIng.withAmount(amount);
		}
		case String str -> {
			ing = ing.withContent(miscHelper.getFluidTagKey(ResourceLocation.parse(str))).withAmount(amount);
		}
		case ResourceLocation location -> {
			ing = ing.withContent(miscHelper.getFluidTagKey(location)).withAmount(amount);
		}
		case TagKey<?> key -> {
			ing = ing.withContent(miscHelper.getFluidTagKey(key.location())).withAmount(amount);
		}
		case FluidStack stack -> {
			if(!stack.isEmpty()) {
				ing = ing.withContent(stack.getFluid()).withAmount(amount);
			}
		}
		case Holder<?> holder -> {
			if(holder.isBound() && holder.value() instanceof Fluid fluid && fluid != Fluids.EMPTY) {
				ing = ing.withContent(fluid).withAmount(amount);
			}
		}
		case Fluid fluid -> {
			if(fluid != Fluids.EMPTY) {
				ing = ing.withContent(fluid).withAmount(amount);
			}
		}
		case IFluidLike fluid -> {
			if(fluid.asFluid() != Fluids.EMPTY) {
				ing = ing.withContent(fluid.asFluid()).withAmount(amount);
			}
		}
		default -> {}
		}
		return ing;
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object thirdOutput, int thirdOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, thirdOutput, thirdOutputCount, time));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerCentrifugeFluidRecipe(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeFluidRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, secondOutput, secondOutputCount, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerCentrifugeFluidRecipe(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeFluidRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerFoundryRecipe(ResourceLocation key, Object input, Object secondInput, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FoundryRecipeSerializer(key, input, secondInput, output, outputCount, time));
	}

	public boolean registerAtomicForgeRecipe(ResourceLocation key, Object input, Object secondInput, Object thirdInput, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AtomicForgeRecipeSerializer(key, input, secondInput, thirdInput, output, outputCount, time));
	}
}
