package thelm.jaopca.compat.enderio;

import java.util.function.Supplier;

import crazypants.enderio.machine.recipe.OreDictionaryRecipeInput;
import crazypants.enderio.machine.recipe.RecipeInput;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.enderio.recipes.SagMillRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class EnderIOHelper {

	public static final EnderIOHelper INSTANCE = new EnderIOHelper();

	private EnderIOHelper() {}

	public RecipeInput getRecipeInput(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getRecipeInput(((Supplier<?>)obj).get(), count);
		}
		if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return new OreDictionaryRecipeInput(
						MiscHelper.INSTANCE.getItemStack(obj, count, true),
						OreDictionary.getOreID((String)obj), -1);
			}
		}
		if(obj instanceof ItemStack) {
			return new RecipeInput(MiscHelper.INSTANCE.resizeItemStack((ItemStack)obj, count));
		}
		if(obj instanceof Item) {
			return new RecipeInput(new ItemStack((Item)obj, count), false);
		}
		if(obj instanceof Block) {
			return new RecipeInput(new ItemStack((Block)obj, count), false);
		}
		if(obj instanceof IItemProvider) {
			return new RecipeInput(new ItemStack(((IItemProvider)obj).asItem(), count), false);
		}
		if(obj instanceof RecipeInput) {
			return (RecipeInput)obj;
		}
		return null;
	}

	public boolean registerSagMillRecipe(String key, Object input, int energy, String bonusType, Object... output) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new SagMillRecipeAction(key, input, energy, bonusType, output));
	}
}
