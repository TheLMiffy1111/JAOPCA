package thelm.jaopca.compat.embers.recipes;

import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.util.FluidOutput;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.embers.EmbersHelper;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputAmount;
	public final Object secondOutput;
	public final int secondOutputAmount;

	public MeltingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputAmount) {
		this(key, input, output, outputAmount, null, 0);
	}

	public MeltingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputAmount, Object secondOutput, int secondOutputAmount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputAmount = outputAmount;
		this.secondOutput = secondOutput;
		this.secondOutputAmount = secondOutputAmount;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		FluidStack secondStack = MiscHelper.INSTANCE.getFluidStack(secondOutput, secondOutputAmount);
		MeltingRecipe recipe = new MeltingRecipe(ing, new FluidOutput(stack), secondStack.isEmpty() ? Optional.empty() : Optional.of(new FluidOutput(secondStack)));
		JsonObject json = MiscHelper.INSTANCE.serialize(MeltingRecipe.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "embers:melting");
		return json;
	}

	public record MeltingRecipe(Ingredient input, FluidOutput output, Optional<FluidOutput> bonus) {
		public static final Codec<MeltingRecipe> CODEC = RecordCodecBuilder.create(instance->instance.group(
				Ingredient.CODEC.fieldOf("input").forGetter(MeltingRecipe::input),
				EmbersHelper.FLUID_OUTPUT_CODEC.fieldOf("output").forGetter(MeltingRecipe::output),
				EmbersHelper.FLUID_OUTPUT_CODEC.optionalFieldOf("bonus").forGetter(MeltingRecipe::bonus)).
				apply(instance, MeltingRecipe::new));
	}
}
