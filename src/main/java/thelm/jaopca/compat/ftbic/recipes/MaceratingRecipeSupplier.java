package thelm.jaopca.compat.ftbic.recipes;

import java.util.function.Supplier;

import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import net.minecraft.util.ResourceLocation;

public class MaceratingRecipeSupplier extends MachineRecipeSupplier {

	public MaceratingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object[] output, double time) {
		super(key, new Object[] {input, inputCount}, new Object[0], output, new Object[0], time);
	}

	@Override
	public Supplier<MachineRecipeSerializer> serializerSupplier() {
		return FTBICRecipes.MACERATING;
	}
}
