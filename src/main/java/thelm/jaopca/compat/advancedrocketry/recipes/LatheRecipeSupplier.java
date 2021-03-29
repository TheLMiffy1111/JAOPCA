package thelm.jaopca.compat.advancedrocketry.recipes;

import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.recipe.RecipeLathe;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class LatheRecipeSupplier extends MachineRecipeSupplier {

	public LatheRecipeSupplier(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		super(key, input, new Object[0], output, new Object[0], time, energy, -1);
	}

	@Override
	public RecipeMachineFactory factoryInstance() {
		return RecipeLathe.INSTANCE;
	}
}
