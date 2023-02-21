package thelm.jaopca.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class ShapedRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object output;
	public final int count;
	public final Object[] input;

	public ShapedRecipeAction(String key, Object output, int count, Object... input) {
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
		int idx = 0;
		if(input[idx] instanceof String[]) {
			idx++;
		}
		else {
			while(input[idx] instanceof String) {
				idx++;
			}
		}
		int end = idx;
		List<Character> keys = new ArrayList<>();
		List<List<?>> inputList = new ArrayList<>();
		for(; idx < input.length; idx += 2)  {
			keys.add((Character)input[idx]);
			Object in = input[idx+1];
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
			Object[] newInput = input.clone();
			for(int i = 0; i < keys.size(); ++i) {
				newInput[end+i*2] = keys.get(i);
				newInput[end+i*2+1] = ins.get(i);
			}
			CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(stack, newInput));
		}
		return true;
	}
}
