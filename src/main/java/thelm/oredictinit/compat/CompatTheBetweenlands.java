package thelm.oredictinit.compat;

import java.lang.reflect.Method;

import thelm.jaopca.api.ICompat;

public class CompatTheBetweenlands implements ICompat {

	@Override
	public String getName() {
		return "thebetweenlands";
	}

	@Override
	public void register() {
		try {
			Class<?> recipeClass = Class.forName("thebetweenlands.common.registries.RecipeRegistry ");
			Method oreMethod = recipeClass.getDeclaredMethod("registerOreDictionary");
			oreMethod.setAccessible(true);
			oreMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
