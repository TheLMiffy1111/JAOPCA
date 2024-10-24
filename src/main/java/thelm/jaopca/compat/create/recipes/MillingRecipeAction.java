package thelm.jaopca.compat.create.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.siepert.createlegacy.util.handlers.recipes.MillingRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class MillingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final int secondOutputChance;
	public final int time;

	public MillingRecipeAction(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondOutputChance, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondOutputChance = secondOutputChance;
		this.time = time;
	}

	@Override
	public boolean register() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		if(stack.isEmpty() && secondStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+output+", "+secondOutput);
		}
		for(ItemStack in : ing.getMatchingStacks()) {
			MillingRecipes.instance().addMillingRecipe(in, stack, secondStack, time, secondOutputChance);
		}
		return true;
	}
}
