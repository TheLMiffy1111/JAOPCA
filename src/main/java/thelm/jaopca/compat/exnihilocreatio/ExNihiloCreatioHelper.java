package thelm.jaopca.compat.exnihilocreatio;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.exnihilocreatio.recipes.SieveRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class ExNihiloCreatioHelper {

	public static final ExNihiloCreatioHelper INSTANCE = new ExNihiloCreatioHelper();

	private ExNihiloCreatioHelper() {}

	public boolean registerSieveRecipe(ResourceLocation key, Object input, Object output, float chance, int mesh) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SieveRecipeAction(key, input, output, chance, mesh));
	}
}
