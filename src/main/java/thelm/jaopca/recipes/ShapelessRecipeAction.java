package thelm.jaopca.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class ShapelessRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object output;
	public final int count;
	public final Object[] input;

	public ShapelessRecipeAction(String key, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.output = output;
		this.count = count;
		this.input = Objects.requireNonNull(input);
	}

	@Override
	public boolean register() {
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		List<List<?>> inputList = new ArrayList<>();
		for(Object in : input) {
			if(in instanceof String) {
				inputList.add(Collections.singletonList(in));
				continue;
			}
			List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(in, 1, true);
			if(ing.isEmpty()) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			else {
				inputList.add(ing);
			}
		}
		for(List<?> ins : MiscHelper.INSTANCE.guavaCartesianProduct(inputList)) {
			CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(stack, ins.toArray()));
		}
		return true;
	}
}
