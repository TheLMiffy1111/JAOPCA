package thelm.jaopca.ingredients;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;

public class UnionIngredient extends CompoundIngredient {

	protected UnionIngredient(List<Ingredient> ingredients) {
		super(ingredients);
	}

	public static UnionIngredient of(List<Ingredient> ingredients) {
		return new UnionIngredient(ingredients);
	}

	public static UnionIngredient of(Ingredient... ingredients) {
		return of(ImmutableList.copyOf(ingredients));
	}
}
