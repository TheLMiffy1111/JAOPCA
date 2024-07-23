package thelm.jaopca.compat.enderio;

import java.util.function.Supplier;

import com.enderio.core.common.util.stackable.Things;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.compat.enderio.recipes.SagMillRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class EnderIOHelper {

	public static final EnderIOHelper INSTANCE = new EnderIOHelper();

	private EnderIOHelper() {}

	public Things getThings(Object obj) {
		Things things = new Things();
		if(obj instanceof Supplier<?>) {
			things.add(getThings(((Supplier<?>)obj).get()));
		}
		else if(obj instanceof Things) {
			things.add((Things)obj);
		}
		else if(obj instanceof String) {
			if(ApiImpl.INSTANCE.getOredict().contains(obj)) {
				things.addOredict((String)obj);
			}
		}
		else if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			if(!stack.isEmpty()) {
				things.add((ItemStack)obj);
			}
		}
		else if(obj instanceof Item) {
			if(obj != Items.AIR) {
				things.add((Item)obj);
			}
		}
		else if(obj instanceof Block) {
			if(obj != Blocks.AIR) {
				things.add((Block)obj);
			}
		}
		else if(obj instanceof IItemProvider) {
			Item item = ((IItemProvider)obj).asItem();
			if(item != Items.AIR) {
				things.add(item);
			}
		}
		return things;
	}

	public boolean registerSagMillRecipe(ResourceLocation key, Object input, int energy, String bonusType, String level, Object... output) {
		return ApiImpl.INSTANCE.registerLateRecipe(key, new SagMillRecipeAction(key, input, energy, bonusType, level, output));
	}
}
