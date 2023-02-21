package thelm.jaopca.compat.engtoolbox.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import emasher.api.CentrifugeRecipeRegistry;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final int secondOutputChance;

	public CentrifugeRecipeAction(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int secondOutputChance) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondOutputChance = secondOutputChance;
	}

	@Override
	public boolean register() {
		ItemStack stack1 = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack1 == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack stack2 = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount, false);
		if(stack2 == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+secondOutput);
		}
		if(input instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(input)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			CentrifugeRecipeRegistry.registerRecipe((String)input, stack1, stack2, secondOutputChance);
		}
		else {
			List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, false);
			if(ing.isEmpty()) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			for(ItemStack in : ing) {
				CentrifugeRecipeRegistry.registerRecipe(in, stack1, stack2, secondOutputChance);
			}
		}
		return true;
	}
}
