package thelm.jaopca.compat.factorization.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import factorization.oreprocessing.TileEntityCrystallizer;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class CrystallizerRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputCount;
	public final Object solution;
	public final int solutionCount;
	public final Object output;
	public final float outputChance;

	public CrystallizerRecipeAction(String key, Object input, int inputCount, Object solution, int solutionCount, Object output, float outputChance) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.solution = solution;
		this.solutionCount = solutionCount;
		this.output = output;
		this.outputChance = outputChance;
	}

	@Override
	public boolean register() {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, inputCount, true);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> solutionIng = MiscHelper.INSTANCE.getItemStacks(solution, solutionCount, true);
		if(solutionIng.isEmpty()) {
			solutionIng.add(null);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, 1, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(ItemStack in : ing) {
			for(ItemStack sol : solutionIng) {
				TileEntityCrystallizer.addRecipe(in, stack, outputChance, sol);
			}
		}
		return true;
	}
}
