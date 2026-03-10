package thelm.jaopca.compat.crossroads.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public class MillRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object[] output;

	public MillRecipeSerializer(ResourceLocation key, Object input, Object... output) {
		this(key, "", input, output);
	}

	public MillRecipeSerializer(ResourceLocation key, String group, Object input, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> stacks = new ArrayList<>();
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
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			stacks.add(stack);
		}
		if(stacks.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		MillRec recipe = new MillRec(group, ing, stacks);
		JsonObject json = MiscHelper.INSTANCE.serialize(MillRec.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "crossroads:mill");
		return json;
	}

	public record MillRec(String group, Ingredient input, List<ItemStack> output) {
		public static final Codec<MillRec> CODEC = RecordCodecBuilder.create(instance->instance.group(
				CraftingUtil.recipeGroupFieldCodec().forGetter(MillRec::group),
				Ingredient.CODEC.fieldOf("input").forGetter(MillRec::input),
				ItemStack.CODEC.listOf(1, 3).fieldOf("output").forGetter(MillRec::output)).
				apply(instance, MillRec::new));
	}
}
