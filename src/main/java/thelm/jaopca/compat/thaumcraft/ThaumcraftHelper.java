package thelm.jaopca.compat.thaumcraft;

import java.util.function.Supplier;

import thaumcraft.api.aspects.Aspect;
import thelm.jaopca.compat.thaumcraft.recipes.CrucibleRecipeAction;
import thelm.jaopca.compat.thaumcraft.recipes.SmeltingBonusRecipeAction;
import thelm.jaopca.compat.thaumcraft.recipes.SpecialMiningRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class ThaumcraftHelper {

	public static final ThaumcraftHelper INSTANCE = new ThaumcraftHelper();

	private ThaumcraftHelper() {}

	public Aspect getAspect(Object obj) {
		if(obj instanceof Supplier<?>) {
			return getAspect((Supplier<?>)obj);
		}
		if(obj instanceof String) {
			return Aspect.getAspect((String)obj);
		}
		if(obj instanceof Aspect) {
			return (Aspect)obj;
		}
		return null;
	}

	public boolean registerCrucibleRecipe(String key, String researchReq, Object input, Object[] aspects, Object output, int count) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new CrucibleRecipeAction(key, researchReq, input, aspects, output, count));
	}

	public boolean registerSmeltingBonusRecipe(String key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new SmeltingBonusRecipeAction(key, input, output, count));
	}

	public boolean registerSpecialMiningRecipe(String key, Object input, Object output, int count, float chance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SpecialMiningRecipeAction(key, input, output, count, chance));
	}
}
