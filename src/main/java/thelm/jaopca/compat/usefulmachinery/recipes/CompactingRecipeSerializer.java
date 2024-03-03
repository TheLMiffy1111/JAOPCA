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
import thelm.jaopca.ingredients.EmptyIngredient;
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

	public CompactingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, String mode) {
		this(key, "", input, inputCount, output, outputCount, time, mode);
	}

	public CompactingRecipeSerializer(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, String mode) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.mode = CompactorMode.byName(mode);
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "usefulmachinery:compacting");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		JsonObject ingJson = MiscHelper.INSTANCE.wrapIngredient(ing).toJson().getAsJsonObject();
		ingJson.addProperty("count", inputCount);
		json.add("ingredient", ingJson);
		json.add("result", MiscHelper.INSTANCE.serializeItemStack(stack));
		json.addProperty("processingtime", time);
		json.addProperty("mode", mode.getSerializedName());

		return json;
	}
}
