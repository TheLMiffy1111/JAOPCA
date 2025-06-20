package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import electrodynamics.common.recipe.categories.item2item.specificmachines.MineralCrusherRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.ElectrodynamicsHelper;
import thelm.jaopca.utils.MiscHelper;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.ProbableItem;

public class MineralCrusherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final double secondChance;
	public final double experience;
	public final int time;
	public final double energy;

	public MineralCrusherRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		this(key, "", input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, experience, time, energy);
	}

	public MineralCrusherRecipeSerializer(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		this(key, group, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, experience, time, energy);
	}

	public MineralCrusherRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		this(key, "", input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy);
	}

	public MineralCrusherRecipeSerializer(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
		this.experience = experience;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		CountableIngredient ing = ElectrodynamicsHelper.INSTANCE.getCountableIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		List<ProbableItem> byproducts = secondStack.isEmpty() ? List.of() : List.of(new ProbableItem(secondStack, secondChance));
		MineralCrusherRecipe recipe = new MineralCrusherRecipe(group, List.of(ing), stack, experience, time, energy, byproducts, List.of(), List.of());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
