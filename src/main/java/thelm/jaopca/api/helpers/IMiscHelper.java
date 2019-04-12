package thelm.jaopca.api.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface IMiscHelper {

	ResourceLocation getTagLocation(String form, String material);

	ItemStack getStack(Object obj, int count);

	Ingredient getIngredient(Object obj);
}
