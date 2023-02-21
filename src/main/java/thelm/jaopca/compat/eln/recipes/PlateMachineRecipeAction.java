package thelm.jaopca.compat.eln.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mods.eln.Eln;
import mods.eln.misc.Recipe;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class PlateMachineRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final double power;

	public PlateMachineRecipeAction(String key, Object input, int inputCount, Object output, int outputCount, double power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.power = power;
	}

	@Override
	public boolean register() {
		ItemStack ing = MiscHelper.INSTANCE.getItemStack(input, inputCount, false);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		Eln.instance.plateMachineRecipes.addRecipe(new Recipe(ing, stack, power));
		return true;
	}
}
