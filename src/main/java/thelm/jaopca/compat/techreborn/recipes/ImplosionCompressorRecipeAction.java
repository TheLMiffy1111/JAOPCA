package thelm.jaopca.compat.techreborn.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.recipes.OreRecipeInput;
import techreborn.api.recipe.machines.ImplosionCompressorRecipe;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class ImplosionCompressorRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object[] output;
	public final int time;
	public final int energy;

	public ImplosionCompressorRecipeAction(ResourceLocation key, Object[] input, Object[] output, int time, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.time = time;
		this.energy = energy;
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
			List<Object> list = new ArrayList<>();
			if(in instanceof String) {
				if(!ApiImpl.INSTANCE.getOredict().contains(in)) {
					throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
				}
				list.add(new OreRecipeInput((String)in, inc));
			}
			else {
				Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
				if(ing == null) {
					throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
				}
				for(ItemStack is : ing.getMatchingStacks()) {
					list.add(MiscHelper.INSTANCE.resizeItemStack(is, inc));
				}
			}
			inputs.add(list);
		}
		if(inputs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		List<ItemStack> outputs = new ArrayList<>();
		i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(stack);
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		for(List<Object> ins : Lists.cartesianProduct(inputs)) {
			ImplosionCompressorRecipe recipe = new ImplosionCompressorRecipe(null, null, null, null, time, energy);
			for(Object in : ins) {
				recipe.addInput(in);
			}
			for(ItemStack out : outputs) {
				recipe.addOutput(out);
			}
			RecipeHandler.addRecipe(recipe);
		}
		return true;
	}

}
