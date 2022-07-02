package thelm.jaopca.compat.techreborn.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.ingredients.input.ItemStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.OreDictionaryInputIngredient;
import techreborn.api.recipe.Recipes;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class GrinderRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final int energy;

	public GrinderRecipeAction(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int energy) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public boolean register() {
		List<InputIngredient<?>> ins = new ArrayList<>();
		if(input instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(input)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			ins.add(OreDictionaryInputIngredient.of((String)input, inputCount));
		}
		else {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			for(ItemStack is : ing.getMatchingStacks()) {
				ins.add(ItemStackInputIngredient.of(MiscHelper.INSTANCE.resizeItemStack(is, inputCount)));
			}
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(InputIngredient<?> in : ins) {
			Recipes.grinder.createRecipe().withInput(Collections.singleton(in)).withOutput(stack).
			withOperationDuration(time).withEnergyCostPerTick(energy).register();
		}
		return true;
	}
}
