package thelm.jaopca.compat.nuclearcraftneohaul.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import com.nred.nuclearcraft.recipe.processor.MelterRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class MelterRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final int inputChance;
	public final int inputMin;
	public final Object output;
	public final int outputAmount;
	public final int outputChance;
	public final int outputIncrement;
	public final int outputMin;
	public final double radiation;
	public final double time;
	public final double power;

	public MelterRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, double radiation, double time, double power) {
		this(key, input, inputCount, 100, 0, output, outputAmount, 100, 1, 0, radiation, time, power);
	}

	public MelterRecipeSerializer(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputAmount, double radiation, double time, double power) {
		this(key, input, inputCount, inputChance, inputMin, output, outputAmount, 100, 1, 0, radiation, time, power);
	}

	public MelterRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, int outputChance, int outputIncrement, int outputMin, double radiation, double time, double power) {
		this(key, input, inputCount, 100, 0, output, outputAmount, outputChance, outputIncrement, outputMin, radiation, time, power);
	}

	public MelterRecipeSerializer(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputAmount, int outputChance, int outputIncrement, int outputMin, double radiation, double time, double power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.inputChance = inputChance;
		this.inputMin = inputMin;
		this.output = output;
		this.outputAmount = outputAmount;
		this.outputChance = outputChance;
		this.outputIncrement = outputIncrement;
		this.outputMin = outputMin;
		this.radiation = radiation;
		this.time = time;
		this.power = power;
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
		SizedChanceItemIngredient input = new SizedChanceItemIngredient(ing, inputCount, inputChance, inputMin);
		SizedChanceFluidIngredient output = SizedChanceFluidIngredient.of(stack.getFluid(), stack.getAmount(), outputChance, outputMin, outputIncrement);
		MelterRecipe recipe = new MelterRecipe(List.of(input), List.of(), List.of(), List.of(output), radiation, time, power);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
