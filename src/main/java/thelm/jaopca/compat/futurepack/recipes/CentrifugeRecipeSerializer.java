package thelm.jaopca.compat.futurepack.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object[] output;
	public final int support;
	public final int time;

	public CentrifugeRecipeSerializer(ResourceLocation key, Object input, int inputCount, int support, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.support = support;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		if(input == Items.AIR || inputCount <= 0) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> results = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe: {}", key);
				continue;
			}
			results.add(stack);
		}
		if(results.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "futurepack:zentrifuge");
		JsonObject inputJson = new JsonObject();
		if(input instanceof String || input instanceof ResourceLocation) {
			inputJson.addProperty("tag", input.toString());
		}
		else if(input instanceof ItemLike) {
			inputJson.addProperty("name", ((ItemLike)input).asItem().getRegistryName().toString());
		}
		inputJson.addProperty("size", inputCount);
		json.add("input", inputJson);
		JsonArray outputJson = new JsonArray();
		for(ItemStack stack : results) {
			JsonObject resultJson = new JsonObject();
			resultJson.addProperty("name", stack.getItem().getRegistryName().toString());
			resultJson.addProperty("size", stack.getCount());
			outputJson.add(resultJson);
		}
		json.add("output", outputJson);
		json.addProperty("support", support);
		json.addProperty("time", time);

		return json;
	}
}
