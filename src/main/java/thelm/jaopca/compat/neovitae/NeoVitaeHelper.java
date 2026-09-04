package thelm.jaopca.compat.neovitae;

import java.util.Map;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.neovitae.recipes.AlchemyTableRecipeSerializer;
import thelm.jaopca.compat.neovitae.recipes.AthanorSerializer;
import thelm.jaopca.utils.ApiImpl;

public class NeoVitaeHelper {

	public static final NeoVitaeHelper INSTANCE = new NeoVitaeHelper();

	private NeoVitaeHelper() {}

	public boolean registerAlchemyTableRecipe(ResourceLocation key, Object[] input, Object output, int count, int cost, int time, int minTier) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlchemyTableRecipeSerializer(key, input, output, count, cost, time, minTier));
	}

	public boolean registerAthanorRecipe(ResourceLocation key, Object input, int inputCount, Object tool, Object fluidInput, int fluidInputAmount, Object[] output, Object fluidOutput, int fluidOutputAmount, Map<SpiritusType, Double> spiritusCosts, boolean spiritusBoost) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AthanorSerializer(key, input, inputCount, tool, fluidInput, fluidInputAmount, output, fluidOutput, fluidOutputAmount, spiritusCosts, spiritusBoost));
	}

	public boolean registerAthanorRecipe(ResourceLocation key, Object input, int inputCount, Object tool, Object[] output, Map<SpiritusType, Double> spiritusCosts, boolean spiritusBoost) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AthanorSerializer(key, input, inputCount, tool, output, spiritusCosts, spiritusBoost));
	}
}
