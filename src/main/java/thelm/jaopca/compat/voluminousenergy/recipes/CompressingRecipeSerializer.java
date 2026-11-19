package thelm.jaopca.compat.voluminousenergy.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.veteam.voluminousenergy.recipe.CompressorRecipe;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.RegistryIngredient;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.voluminousenergy.VoluminousEnergyHelper;
import thelm.jaopca.utils.MiscHelper;

public class CompressingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;

	public CompressingRecipeSerializer(Identifier key, Object input, int inputCount, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		RegistryIngredient ing = VoluminousEnergyHelper.INSTANCE.getRegistryIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, outputCount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CompressorRecipe recipe = new CompressorRecipe(List.of(ing), List.of(stack), time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
