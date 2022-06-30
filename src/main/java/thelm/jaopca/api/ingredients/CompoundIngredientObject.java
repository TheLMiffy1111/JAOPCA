package thelm.jaopca.api.ingredients;

import java.util.Arrays;

import thelm.jaopca.api.ingredients.CompoundIngredientObject.Type;

public record CompoundIngredientObject(Type type, Object... ingredients) {

	public static CompoundIngredientObject of(Type type, Object... ingredients) {
		return new CompoundIngredientObject(type, ingredients);
	}

	public static CompoundIngredientObject union(Object... ingredients) {
		return new CompoundIngredientObject(Type.UNION, ingredients);
	}

	public static CompoundIngredientObject intersection(Object... ingredients) {
		return new CompoundIngredientObject(Type.INTERSECTION, ingredients);
	}

	public static CompoundIngredientObject difference(Object... ingredients) {
		return new CompoundIngredientObject(Type.DIFFERENCE, ingredients);
	}

	@Override
	public String toString() {
		return "CompoundIngredientObject[type="+type+", ingredients="+Arrays.deepToString(ingredients)+"]";
	}

	public enum Type {
		UNION, INTERSECTION, DIFFERENCE;
	}
}
