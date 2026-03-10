package thelm.jaopca.compat.crossroads.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class StampMillRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int count;

	public StampMillRecipeSerializer(ResourceLocation key, Object input, Object output, int count) {
		this(key, "", input, output, count);
	}

	public StampMillRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.count = count;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		StampMillRec recipe = new StampMillRec(group, ing, stack);
		JsonObject json = MiscHelper.INSTANCE.serialize(StampMillRec.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "crossroads:stamp_mill");
		return json;
	}

	public record StampMillRec(String group, Ingredient input, ItemStack output) {
		public static final Codec<StampMillRec> CODEC = RecordCodecBuilder.create(instance->instance.group(
				CraftingUtil.recipeGroupFieldCodec().forGetter(StampMillRec::group),
				Ingredient.CODEC.fieldOf("ingredient").forGetter(StampMillRec::input),
				ItemStack.CODEC.fieldOf("output").forGetter(StampMillRec::output)).
				apply(instance, StampMillRec::new));
	}
}
