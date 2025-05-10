package thelm.jaopca.compat.techreborn.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CompressorRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int power;
	public final int time;

	public CompressorRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int power, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.power = power;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		SizedIngredient ing = MiscHelper.INSTANCE.getSizedIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		RebornRecipe recipe = new RebornRecipe(List.of(ing), List.of(stack), power, time);
		JsonObject json = MiscHelper.INSTANCE.serialize(RebornRecipe.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "techreborn:compressor");
		return json;
	}
}
