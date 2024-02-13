package thelm.jaopca.compat.ganysnether;

import java.util.function.Supplier;

import ganymedes01.ganysnether.core.utils.xml.OreStack;
import ganymedes01.ganysnether.recipes.RecipeInput;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.ganysnether.recipes.MagmaticCentrifugeRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class GanysNetherHelper {

	public static final GanysNetherHelper INSTANCE = new GanysNetherHelper();

	private GanysNetherHelper() {}

	public RecipeInput<?> getRecipeInput(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getRecipeInput(((Supplier<?>)obj).get(), count);
		}
		if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				return new RecipeInput.RecipeInputOre(new OreStack((String)obj, count));
			}
		}
		if(obj instanceof OreStack) {
			return new RecipeInput.RecipeInputOre(new OreStack(((OreStack)obj).ore, count));
		}
		if(obj instanceof ItemStack) {
			return new RecipeInput.RecipeInputItemStack(MiscHelper.INSTANCE.resizeItemStack((ItemStack)obj, count));
		}
		if(obj instanceof Item) {
			return new RecipeInput.RecipeInputItemStack(new ItemStack((Item)obj, count, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof Block) {
			return new RecipeInput.RecipeInputItemStack(new ItemStack((Block)obj, count, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof IItemProvider) {
			return new RecipeInput.RecipeInputItemStack(new ItemStack(((IItemProvider)obj).asItem(), count, OreDictionary.WILDCARD_VALUE));
		}
		if(obj instanceof RecipeInput<?>) {
			return (RecipeInput<?>)obj;
		}
		return null;
	}

	public boolean registerMagmaticCentrifugeRecipe(String key, Object firstInput, int firstInputCount, Object secondInput, int secondInputCount, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MagmaticCentrifugeRecipeAction(key, firstInput, firstInputCount, secondInput, secondInputCount, output));
	}
}
