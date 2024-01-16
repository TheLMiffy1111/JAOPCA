package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import electrodynamics.common.recipe.categories.item2item.specificmachines.MineralGrinderRecipe;
import electrodynamics.common.recipe.recipeutils.CountableIngredient;
import electrodynamics.common.recipe.recipeutils.ProbableFluid;
import electrodynamics.common.recipe.recipeutils.ProbableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.electrodynamics.ElectrodynamicsHelper;
import thelm.jaopca.utils.MiscHelper;

public class MineralGrinderRecipeSupplier implements Supplier<MineralGrinderRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
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

	public MineralGrinderRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		this(key, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, experience, time, energy);
	}

	public MineralGrinderRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		this.key = Objects.requireNonNull(key);
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
	public MineralGrinderRecipe get() {
		CountableIngredient ing = ElectrodynamicsHelper.INSTANCE.getCountableIngredient(input, inputCount);
		if(ing.fetchCountedStacks().isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		ProbableItem[] itembi = secondStack.isEmpty() ? new ProbableItem[0] : new ProbableItem[] {new ProbableItem(secondStack, secondChance)};
		return new MineralGrinderRecipe(key, new CountableIngredient[] {ing}, stack, experience, time, energy, itembi, new ProbableFluid[0]);
	}
}
