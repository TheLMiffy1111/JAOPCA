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

public class ImplosionCompressorRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object secondInput;
	public final int secondInputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final int power;
	public final int time;

	public ImplosionCompressorRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, int power, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.secondInput = secondInput;
		this.secondInputCount = secondInputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.power = power;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		SizedIngredient ing = MiscHelper.INSTANCE.getSizedIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+input+", "+secondInput);
		}
		SizedIngredient secondIng = MiscHelper.INSTANCE.getSizedIngredient(secondInput, secondInputCount);
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		List<SizedIngredient> inputs = secondIng != null ? List.of(ing, secondIng) : List.of(ing);
		List<ItemStack> outputs = !secondStack.isEmpty() ? List.of(stack, secondStack) : List.of(stack);
		RebornRecipe recipe = new RebornRecipe(inputs, outputs, power, time);
		JsonObject json = MiscHelper.INSTANCE.serialize(RebornRecipe.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "techreborn:implosion_compressor");
		return json;
	}
}
