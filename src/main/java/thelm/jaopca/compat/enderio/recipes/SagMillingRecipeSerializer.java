package thelm.jaopca.compat.enderio.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class SagMillingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final int energy;
	public final BonusType bonusType;
	public final Object[] output;

	public SagMillingRecipeSerializer(Identifier key, Object input, int energy, String bonusType, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.energy = energy;
		this.bonusType = Objects.requireNonNull(BonusType.valueOf(bonusType.toUpperCase(Locale.US)));
		this.output = output;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<OutputItem> outputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Float chance = 1F;
			if(i < output.length && output[i] instanceof Float) {
				chance = (Float)output[i];
				++i;
			}
			ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(out, count);
			if(stack == null) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(new OutputItem(stack, chance, false));
		}
		SagMillingRecipe recipe = new SagMillingRecipe(ing, outputs, energy, bonusType);
		JsonObject json = MiscHelper.INSTANCE.serialize(SagMillingRecipe.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "enderio:sag_milling");
		return json;
	}

	// recreation of SAG mill recipe structs in case Ender IO moves them again
	public record SagMillingRecipe(Ingredient input, List<OutputItem> outputs, int energy, BonusType bonusType) {
		public static final Codec<SagMillingRecipe> CODEC = RecordCodecBuilder.create(instance->instance.group(
				Ingredient.CODEC.fieldOf("input").forGetter(SagMillingRecipe::input),
				OutputItem.CODEC.listOf().fieldOf("outputs").forGetter(SagMillingRecipe::outputs),
				Codec.INT.fieldOf("energy").forGetter(SagMillingRecipe::energy),
				BonusType.CODEC.optionalFieldOf("bonus", BonusType.MULTIPLY_OUTPUT).forGetter(SagMillingRecipe::bonusType)).
				apply(instance, SagMillingRecipe::new));
	}

	public record OutputItem(ItemStackTemplate output, float chance, boolean isOptional) {
		public static final Codec<OutputItem> CODEC = RecordCodecBuilder.create(instance->instance.group(
				ItemStackTemplate.CODEC.fieldOf("item").forGetter(OutputItem::output),
				Codec.FLOAT.optionalFieldOf("chance", 1F).forGetter(OutputItem::chance),
				Codec.BOOL.optionalFieldOf("optional", false).forGetter(OutputItem::isOptional)).
				apply(instance, OutputItem::new));
	}

	public enum BonusType implements StringRepresentable {
		NONE, MULTIPLY_OUTPUT, CHANCE_ONLY;

		public static final Codec<BonusType> CODEC = StringRepresentable.fromEnum(BonusType::values);

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.US);
		}
	}
}
