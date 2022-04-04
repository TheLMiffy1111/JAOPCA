package thelm.jaopca.ingredients;

import net.minecraftforge.common.crafting.CraftingHelper;

public class IngredientSerializers {

	public static void init() {
		CraftingHelper.register(EmptyIngredient.ID, EmptyIngredient.SERIALIZER);
	}
}
