package thelm.jaopca.ingredients;

import net.minecraftforge.common.crafting.CraftingHelper;

public class IngredientSerializers {

	public static void init() {
		CraftingHelper.register(EmptyIngredient.ID, EmptyIngredient.SERIALIZER);
		CraftingHelper.register(IntersectionIngredient.ID, IntersectionIngredient.SERIALIZER);
		CraftingHelper.register(DifferenceIngredient.ID, DifferenceIngredient.SERIALIZER);
	}
}
