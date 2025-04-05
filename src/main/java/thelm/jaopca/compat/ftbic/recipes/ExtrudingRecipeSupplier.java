package thelm.jaopca.compat.ftbic.recipes;

import java.util.function.Supplier;

import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.minecraft.util.ResourceLocation;

public class ExtrudingRecipeSupplier extends MachineRecipeSupplier {

	public ExtrudingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double time) {
		super(key, new Object[] {input, inputCount}, ObjectArrays.EMPTY_ARRAY, new Object[] {output, outputCount, 1D}, ObjectArrays.EMPTY_ARRAY, time);
	}

	@Override
	public Supplier<MachineRecipeSerializer> serializerSupplier() {
		return FTBICRecipes.EXTRUDING;
	}
}
