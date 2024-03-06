package thelm.jaopca.compat.modernindustrialization.recipes;

import aztech.modern_industrialization.machines.recipe.MIRecipeJson;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.MachineRecipeType;

public class MIRecipeConstructor extends MIRecipeJson<MIRecipeConstructor> {

	public MIRecipeConstructor(MachineRecipeType machineRecipeType, int eu, int duration) {
		super(machineRecipeType, eu, duration);
	}

	public MachineRecipe recipe() {
		return recipe;
	}
}
