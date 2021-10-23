package thelm.jaopca.compat.assemblylinemachines;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.assemblylinemachines.recipes.GrinderRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class AssemblyLineMachinesHelper {

	public static final AssemblyLineMachinesHelper INSTANCE = new AssemblyLineMachinesHelper();

	private AssemblyLineMachinesHelper() {}

	public boolean registerGrinderRecipe(ResourceLocation key, Object input, Object output, int outputCount, int grinds, int tier, boolean requiresMachine) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrinderRecipeSupplier(key, input, output, outputCount, grinds, tier, requiresMachine));
	}
}
