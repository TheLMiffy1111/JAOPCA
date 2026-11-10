package thelm.jaopca.compat.hephaestus;

import java.util.function.ToIntFunction;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import thelm.jaopca.compat.hephaestus.recipes.CastingBasinRecipeSerializer;
import thelm.jaopca.compat.hephaestus.recipes.CastingTableRecipeSerializer;
import thelm.jaopca.compat.hephaestus.recipes.MeltingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class HephaestusHelper {

	public static final HephaestusHelper INSTANCE = new HephaestusHelper();

	private HephaestusHelper() {}

	public boolean registerMeltingRecipe(Identifier key, Object input, Object fuel, int fuelAmount, Object output, int outputAmount, ToIntFunction<FluidStackTemplate> temperature, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, input, fuel, fuelAmount, output, outputAmount, temperature, time));
	}

	public boolean registerCastingTableRecipe(Identifier key, Object cast, Object input, int inputAmount, Object output, int outputCount, int time, boolean consumeCast) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingTableRecipeSerializer(key, cast, input, inputAmount, output, outputCount, time, consumeCast));
	}

	public boolean registerCastingBasinRecipe(Identifier key, Object cast, Object input, int inputAmount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingBasinRecipeSerializer(key, cast, input, inputAmount, output, outputCount, time));
	}
}
