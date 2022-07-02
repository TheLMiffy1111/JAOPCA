package thelm.jaopca.compat.skyresources.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bartz24.skyresources.recipe.ProcessRecipeManager;
import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class FusionRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object output;
	public final int count;
	public final float usage;
	public final Object[] input;

	public FusionRecipeAction(ResourceLocation key, Object output, int count, float usage, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.output = output;
		this.count = count;
		this.usage = usage;
		this.input = input;
	}

	@Override
	public boolean register() {
		List<List<Object>> inputs = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer inc = 1;
			if(i < input.length && input[i] instanceof Integer) {
				inc = (Integer)input[i];
				++i;
			}
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			List<Object> list = new ArrayList<>();
			for(ItemStack is : ing.getMatchingStacks()) {
				list.add(MiscHelper.INSTANCE.resizeItemStack(is, inc));
			}
			inputs.add(list);
		}
		if(inputs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		for(List<Object> in : Lists.cartesianProduct(inputs)) {
			ProcessRecipeManager.fusionRecipes.addRecipe(stack, usage, in);
		}
		return true;
	}
}
