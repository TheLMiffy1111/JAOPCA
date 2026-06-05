package thelm.jaopca.compat.energizedpower.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import me.jddev0.ep.recipe.PulverizerRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class PulverizerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final Object output;
	public final double[] chances;
	public final double[] chancesAdvanced;
	public final Object secondOutput;
	public final double[] secondChances;
	public final double[] secondChancesAdvanced;

	public PulverizerRecipeSerializer(Identifier key, Object input, Object output, double[] chances, double[] chancesAdvanced) {
		this(key, input, output, chances, chancesAdvanced, ItemStack.EMPTY, DoubleArrays.EMPTY_ARRAY, DoubleArrays.EMPTY_ARRAY);
	}

	public PulverizerRecipeSerializer(Identifier key, Object input, Object output, double[] chances, double[] chancesAdvanced, Object secondOutput, double[] secondChances, double[] secondChancesAdvanced) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.chances = chances;
		this.chancesAdvanced = chancesAdvanced;
		this.secondOutput = secondOutput;
		this.secondChances = secondChances;
		this.secondChancesAdvanced = secondChancesAdvanced;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, 1);
		if(stack == null || chances.length == 0) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStackTemplate secondStack = MiscHelper.INSTANCE.getItemStackTemplate(secondOutput, 1);
		PulverizerRecipe recipe = new PulverizerRecipe(
				new PulverizerRecipe.OutputItemStackWithPercentages(stack, chances, chancesAdvanced),
				new PulverizerRecipe.OutputItemStackWithPercentages(secondStack, secondChances, secondChancesAdvanced),
				ing);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
