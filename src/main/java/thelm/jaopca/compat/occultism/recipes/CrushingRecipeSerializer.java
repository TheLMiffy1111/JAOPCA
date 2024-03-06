package thelm.jaopca.compat.occultism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final boolean ignoreMultiplier;

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int time, boolean ignoreMultiplier) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.ignoreMultiplier = ignoreMultiplier;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CrushingRecipeObject recipe = new CrushingRecipeObject(ing, stack, time, ignoreMultiplier);
		return MiscHelper.INSTANCE.serialize(CrushingRecipeObject.CODEC, recipe);
	}

	// Occultism crushing recipe codec is broken for serialization
	public static record CrushingRecipeObject(String type, Ingredient ingredient, ItemStack result, int crushingTime, boolean ignoreCrushingMultiplier) {
		public static final Codec<CrushingRecipeObject> CODEC = RecordCodecBuilder.create(
				instance->instance.group(
						Codec.STRING.fieldOf("type").forGetter(CrushingRecipeObject::type),
						Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CrushingRecipeObject::ingredient),
						ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(CrushingRecipeObject::result),
						Codec.INT.fieldOf("crushing_time").forGetter(CrushingRecipeObject::crushingTime),
						Codec.BOOL.fieldOf("ignore_crushing_multiplier").forGetter(CrushingRecipeObject::ignoreCrushingMultiplier)).
				apply(instance, CrushingRecipeObject::new));

		public CrushingRecipeObject(Ingredient ingredient, ItemStack result, int crushingTime, boolean ignoreCrushingMultiplier) {
			this("occultism:crushing", ingredient, result, crushingTime, ignoreCrushingMultiplier);
		}
	}
}
