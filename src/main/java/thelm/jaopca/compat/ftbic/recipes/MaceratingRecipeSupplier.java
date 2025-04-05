package thelm.jaopca.compat.ftbic.recipes;

import java.util.function.Supplier;

import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.minecraft.util.ResourceLocation;

public class MaceratingRecipeSupplier extends MachineRecipeSupplier {

	public MaceratingRecipeSupplier(ResourceLocation key, Object input, int inputCount, Object[] output, double time) {
		super(key, new Object[] {input, inputCount}, ObjectArrays.EMPTY_ARRAY, output, ObjectArrays.EMPTY_ARRAY, time);
	}

	@Override
	public Supplier<MachineRecipeSerializer> serializerSupplier() {
		return FTBICRecipes.MACERATING;
	}
}
