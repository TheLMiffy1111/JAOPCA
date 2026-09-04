package thelm.jaopca.compat.nuclearcraftneohaul.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.nred.nuclearcraft.recipe.SizedChanceItemIngredient;
import com.nred.nuclearcraft.recipe.processor.ManufactoryRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class ManufactoryRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final int inputChance;
	public final int inputMin;
	public final Object output;
	public final int outputCount;
	public final int outputChance;
	public final int outputMin;
	public final double radiation;
	public final double time;
	public final double power;

	public ManufactoryRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double radiation, double time, double power) {
		this(key, input, inputCount, 100, 0, output, outputCount, 100, 0, radiation, time, power);
	}

	public ManufactoryRecipeSerializer(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputCount, double radiation, double time, double power) {
		this(key, input, inputCount, inputChance, inputMin, output, outputCount, 100, 0, radiation, time, power);
	}

	public ManufactoryRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		this(key, input, inputCount, 100, 0, output, outputCount, outputChance, outputMin, radiation, time, power);
	}

	public ManufactoryRecipeSerializer(ResourceLocation key, Object input, int inputCount, int inputChance, int inputMin, Object output, int outputCount, int outputChance, int outputMin, double radiation, double time, double power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.inputChance = inputChance;
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
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		SizedChanceItemIngredient input = new SizedChanceItemIngredient(ing, inputCount, inputChance, inputMin);
		SizedChanceItemIngredient output = new SizedChanceItemIngredient(Ingredient.of(stack.getItem()), stack.getCount(), outputChance, outputMin);
		ManufactoryRecipe recipe = new ManufactoryRecipe(List.of(input), List.of(output), List.of(), List.of(), radiation, time, power);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
