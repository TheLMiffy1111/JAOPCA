package thelm.jaopca.compat.engtoolbox.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import emasher.api.MultiSmelterRecipeRegistry;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class MultiSmelterRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object firstInput;
	public final Object secondInput;
	public final Object output;
	public final int count;

	public MultiSmelterRecipeAction(String key, Object firstInput, Object secondInput, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.firstInput = firstInput;
		this.secondInput = secondInput;
		this.output = output;
		this.count = count;
	}

	@Override
	public boolean register() {
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		if(firstInput instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(firstInput)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+firstInput);
			}
			if(secondInput instanceof String) {
				if(!ApiImpl.INSTANCE.getOredict().contains(secondInput)) {
					throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+firstInput);
				}
				MultiSmelterRecipeRegistry.registerRecipe((String)firstInput, (String)secondInput, stack);
			}
			else {
				List<ItemStack> ing2 = MiscHelper.INSTANCE.getItemStacks(secondInput, 1, false);
				if(ing2.isEmpty()) {
					throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+secondInput);
				}
				for(ItemStack in : ing2) {
					MultiSmelterRecipeRegistry.registerRecipe((String)firstInput, in, stack);
				}
			}
		}
		else {
			List<ItemStack> ing1 = MiscHelper.INSTANCE.getItemStacks(firstInput, 1, false);
			if(ing1.isEmpty()) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+firstInput);
			}
			if(secondInput instanceof String) {
				if(!ApiImpl.INSTANCE.getOredict().contains(secondInput)) {
					throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+firstInput);
				}
				for(ItemStack in : ing1) {
					MultiSmelterRecipeRegistry.registerRecipe(in, (String)secondInput, stack);
				}
			}
			else {
				List<ItemStack> ing2 = MiscHelper.INSTANCE.getItemStacks(secondInput, 1, false);
				if(ing2.isEmpty()) {
					throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+secondInput);
				}
				for(ItemStack in1 : ing1) {
					for(ItemStack in2 : ing1) {
						MultiSmelterRecipeRegistry.registerRecipe(in1, in2, stack);
					}
				}
			}
		}
		return true;
	}
}
