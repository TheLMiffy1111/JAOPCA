package thelm.jaopca.compat.embers.recipes;

import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.recipe.FluidIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.embers.EmbersHelper;
import thelm.jaopca.utils.MiscHelper;

public class StampingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object stamp;
	public final Object itemInput;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputCount;

	public StampingRecipeSerializer(ResourceLocation key, Object stamp, Object itemInput, Object output, int outputCount) {
		this(key, stamp, itemInput, null, 0, output, outputCount);
	}

	public StampingRecipeSerializer(ResourceLocation key, Object stamp, Object fluidInput, int fluidInputAmount, Object output, int outputCount) {
		this(key, stamp, null, fluidInput, fluidInputAmount, output, outputCount);
	}

	public StampingRecipeSerializer(ResourceLocation key, Object stamp, Object itemInput, Object fluidInput, int fluidInputAmount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.stamp = stamp;
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public JsonElement get() {
		Ingredient stampIng = MiscHelper.INSTANCE.getIngredient(stamp);
		if(stampIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+stamp);
		}
		Ingredient itemIng = MiscHelper.INSTANCE.getIngredient(itemInput);
		FluidIngredient fluidIng = EmbersHelper.INSTANCE.getFluidIngredient(fluidInput, fluidInputAmount);
		if(itemIng == null && fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+itemInput+", "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		StampingRecipe recipe = new StampingRecipe(stampIng, Optional.ofNullable(itemIng), Optional.ofNullable(fluidIng), stack);
		JsonObject json = MiscHelper.INSTANCE.serialize(StampingRecipe.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "embers:stamping");
		return json;
	}

	public record StampingRecipe(Ingredient stamp, Optional<Ingredient> input, Optional<FluidIngredient> fluid, ItemStack output) {
		public static final Codec<StampingRecipe> CODEC = RecordCodecBuilder.create(instance->instance.group(
				Ingredient.CODEC.fieldOf("stamp").forGetter(StampingRecipe::stamp),
				Ingredient.CODEC.optionalFieldOf("input").forGetter(StampingRecipe::input),
				EmbersHelper.FLUID_INGREDIENT_CODEC.optionalFieldOf("fluid").forGetter(StampingRecipe::fluid),
				ItemStack.CODEC.fieldOf("output").forGetter(StampingRecipe::output)).
				apply(instance, StampingRecipe::new));
	}
}
