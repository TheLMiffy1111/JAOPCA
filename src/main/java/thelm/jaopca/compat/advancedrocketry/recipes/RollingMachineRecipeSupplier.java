package thelm.jaopca.compat.advancedrocketry.recipes;

import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.recipe.RecipeRollingMachine;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RollingMachineRecipeSupplier extends MachineRecipeSupplier {

	public RollingMachineRecipeSupplier(ResourceLocation key, Object[] itemInput, Object fluidInput, int fluidInputAmount, Object[] output, int time, int energy) {
		super(key, itemInput, new Object[] {fluidInput, fluidInputAmount}, output, new Object[0], time, energy, -1);
	}

	@Override
	public RecipeMachineFactory factoryInstance() {
		return RecipeRollingMachine.INSTANCE;
	}
}
