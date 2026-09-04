package thelm.jaopca.compat.oritech;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.oritech.recipes.AtomicForgeRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.CentrifugeFluidRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.CentrifugeRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.FoundryRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.FragmentForgeRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.PulverizerRecipeSerializer;
import thelm.jaopca.compat.oritech.recipes.RefineryRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class OritechHelper {

	public static final OritechHelper INSTANCE = new OritechHelper();

	private OritechHelper() {}

	public boolean registerPulverizerRecipe(Identifier key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerPulverizerRecipe(Identifier key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerFragmentForgeRecipe(Identifier key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object thirdOutput, int thirdOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FragmentForgeRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, thirdOutput, thirdOutputCount, time));
	}

	public boolean registerFragmentForgeRecipe(Identifier key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FragmentForgeRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerFragmentForgeRecipe(Identifier key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FragmentForgeRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerCentrifugeRecipe(Identifier key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, output, outputCount, secondOutput, secondOutputCount, time));
	}

	public boolean registerCentrifugeRecipe(Identifier key, Object input, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, output, outputCount, time));
	}

	public boolean registerCentrifugeFluidRecipe(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeFluidRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, secondOutput, secondOutputCount, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerCentrifugeFluidRecipe(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeFluidRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerFoundryRecipe(Identifier key, Object input, Object secondInput, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FoundryRecipeSerializer(key, input, secondInput, output, outputCount, time));
	}

	public boolean registerAtomicForgeRecipe(Identifier key, Object input, Object secondInput, Object thirdInput, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AtomicForgeRecipeSerializer(key, input, secondInput, thirdInput, output, outputCount, time));
	}

	public boolean registerRefineryRecipe(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, Object secondFluidOutput, int secondFluidOutputAmount, Object thirdFluidOutput, int thirdFluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RefineryRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, secondFluidOutput, secondFluidOutputAmount, thirdFluidOutput, thirdFluidOutputAmount, time));
	}

	public boolean registerRefineryRecipe(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, Object secondFluidOutput, int secondFluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RefineryRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, secondFluidOutput, secondFluidOutputAmount, time));
	}

	public boolean registerRefineryRecipe(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RefineryRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, time));
	}

	public boolean registerRefineryRecipe(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RefineryRecipeSerializer(key, input, fluidInput, fluidInputAmount, output, outputCount, time));
	}
}
