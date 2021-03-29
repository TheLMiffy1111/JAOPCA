package thelm.jaopca.compat.advancedrocketry.recipes;

import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.recipe.RecipeSmallPresser;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class SmallPlateRecipeSupplier extends MachineRecipeSupplier {

	public SmallPlateRecipeSupplier(ResourceLocation key, Object input, Object output, int outputCount) {
		super(key, new Object[] {input}, new Object[0], new Object[] {output, outputCount}, new Object[0], 300, 20, -1);
	}

	@Override
	public RecipeMachineFactory factoryInstance() {
		return RecipeSmallPresser.INSTANCE;
	}
}
