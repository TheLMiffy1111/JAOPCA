package thelm.jaopca.compat.nuclearcraftneohaul.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import com.nred.nuclearcraft.recipe.processor.IngotFormerRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class IngotFormerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final int inputChance;
	public final int inputIncrement;
	public final int inputMin;
	public final Object output;
	public final int outputCount;
	public final int outputChance;
	public final int outputMin;
	public final double radiation;
	public final double time;
	public final double power;

	public IngotFormerRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object output, int outputAmount, double radiation, double time, double power) {
		this(key, input, inputAmount, 100, 1, 0, output, outputAmount, 100, 0, radiation, time, power);
	}

	public IngotFormerRecipeSerializer(ResourceLocation key, Object input, int inputAmount, int inputChance, int inputIncrement, int inputMin, Object output, int outputAmount, double radiation, double time, double power) {
		this(key, input, inputAmount, inputChance, inputIncrement, inputMin, output, outputAmount, 100, 0, radiation, time, power);
	}

	public IngotFormerRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object output, int outputAmount, int outputChance, int outputMin, double radiation, double time, double power) {
		this(key, input, inputAmount, 100, 1, 0, output, outputAmount, outputChance, outputMin, radiation, time, power);
	}

	public IngotFormerRecipeSerializer(ResourceLocation key, Object input, int inputAmount, int inputChance, int inputIncrement, int inputMin, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.inputChance = inputChance;
		this.inputIncrement = inputIncrement;
		this.inputMin = inputMin;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.outputMin = outputMin;
		this.radiation = radiation;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		FluidIngredient ing = MiscHelper.INSTANCE.getFluidIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		SizedChanceFluidIngredient input = new SizedChanceFluidIngredient(ing, inputAmount, inputChance, inputMin, inputIncrement);
		SizedChanceItemIngredient output = new SizedChanceItemIngredient(Ingredient.of(stack.getItem()), stack.getCount(), outputChance, outputMin);
		IngotFormerRecipe recipe = new IngotFormerRecipe(List.of(), List.of(output), List.of(input), List.of(), radiation, time, power);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
