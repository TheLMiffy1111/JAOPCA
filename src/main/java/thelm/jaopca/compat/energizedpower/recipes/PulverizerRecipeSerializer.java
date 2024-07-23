package thelm.jaopca.compat.energizedpower.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import me.jddev0.ep.recipe.PulverizerRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class PulverizerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final double[] chances;
	public final double[] chancesAdvanced;
	public final Object secondOutput;
	public final double[] secondChances;
	public final double[] secondChancesAdvanced;

	public PulverizerRecipeSerializer(ResourceLocation key, Object input, Object output, double[] chances, double[] chancesAdvanced) {
		this(key, input, output, chances, chancesAdvanced, ItemStack.EMPTY, new double[0], new double[0]);
	}

	public PulverizerRecipeSerializer(ResourceLocation key, Object input, Object output, double[] chances, double[] chancesAdvanced, Object secondOutput, double[] secondChances, double[] secondChancesAdvanced) {
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
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, 1);
		if(stack.isEmpty() || chances.length == 0) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, 1);
		// Energized Power pulverizer recipe codec is broken for serialization
		double[] secondChancesFix = secondChances;
		double[] secondChancesAdvancedFix = secondChancesAdvanced;
		if(secondStack.isEmpty()) {
			secondStack = new ItemStack(Items.BARRIER);
			secondChancesFix = DoubleArrays.EMPTY_ARRAY;
			secondChancesAdvancedFix = DoubleArrays.EMPTY_ARRAY;
		}
		PulverizerRecipe recipe = new PulverizerRecipe(
				new PulverizerRecipe.OutputItemStackWithPercentages(stack, chances, chancesAdvanced),
				new PulverizerRecipe.OutputItemStackWithPercentages(secondStack, secondChancesFix, secondChancesAdvancedFix),
				ing);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
