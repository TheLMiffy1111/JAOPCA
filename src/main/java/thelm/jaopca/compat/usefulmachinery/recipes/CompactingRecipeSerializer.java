package thelm.jaopca.compat.usefulmachinery.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;
import themcbros.usefulmachinery.machine.CompactorMode;

public class CompactingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final CompactorMode mode;

	public CompactingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int mode) {
		this(key, "", input, inputCount, output, outputCount, time, mode);
	}

	public CompactingRecipeSerializer(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, int mode) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.mode = CompactorMode.byIndex(mode);
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "usefulmachinery:compacting");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		json.add("ingredient", ing.toJson());
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("item", stack.getItem().getRegistryName().toString());
		resultJson.addProperty("count", stack.getCount());
		json.add("result", resultJson);
		json.addProperty("processingtime", time);
		json.addProperty("mode", mode.getSerializedName());

		return json;
	}
}
