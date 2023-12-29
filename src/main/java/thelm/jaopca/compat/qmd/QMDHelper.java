package thelm.jaopca.compat.qmd;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.qmd.recipes.OreLeacherRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class QMDHelper {

	public static final QMDHelper INSTANCE = new QMDHelper();

	private QMDHelper() {}

	public boolean registerOreLeacherRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object[] fluidInput, Object[] output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreLeacherRecipeAction(key, itemInput, itemInputCount, fluidInput, output));
	}
}
