package thelm.jaopca.compat.hephaestus;

import java.util.function.ToIntFunction;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.compat.hephaestus.recipes.CastingBasinRecipeSerializer;
import thelm.jaopca.compat.hephaestus.recipes.CastingTableRecipeSerializer;
import thelm.jaopca.compat.hephaestus.recipes.MeltingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class HephaestusHelper {

	public static final HephaestusHelper INSTANCE = new HephaestusHelper();

	private HephaestusHelper() {}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, Object fuel, int fuelAmount, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, input, fuel, fuelAmount, output, outputAmount, temperature, time));
	}

	public boolean registerCastingTableRecipe(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, int time, boolean consumeCast) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingTableRecipeSerializer(key, cast, input, inputAmount, output, outputCount, time, consumeCast));
	}

	public boolean registerCastingBasinRecipe(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingBasinRecipeSerializer(key, cast, input, inputAmount, output, outputCount, time));
	}
}
