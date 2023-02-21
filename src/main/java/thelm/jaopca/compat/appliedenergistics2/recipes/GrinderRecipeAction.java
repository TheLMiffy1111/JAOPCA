package thelm.jaopca.compat.appliedenergistics2.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import appeng.api.AEApi;
import appeng.api.features.IGrinderRegistry;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class GrinderRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondOutputChance;
	public final Object thirdOutput;
	public final int thirdOutputCount;
	public final float thirdOutputChance;
	public final int turns;

	public GrinderRecipeAction(String key, Object input, Object output, int outputCount, int turns) {
		this(key, input, output, outputCount, null, 0, 0, null, 0, 0, turns);
	}

	public GrinderRecipeAction(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int turns) {
		this(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, null, 0, 0, turns);
	}

	public GrinderRecipeAction(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance, int turns) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondOutputChance = secondOutputChance;
		this.thirdOutput = thirdOutput;
		this.thirdOutputCount = thirdOutputCount;
		this.thirdOutputChance = thirdOutputChance;
		this.turns = turns;
	}

	@Override
	public boolean register() {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, true);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack1 = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack1 == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack stack2 = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount, false);
		ItemStack stack3 = MiscHelper.INSTANCE.getItemStack(thirdOutput, thirdOutputCount, false);
		IGrinderRegistry registry = AEApi.instance().registries().grinder();
		for(ItemStack in : ing) {
			registry.addRecipe(in, stack1, stack2, secondOutputChance, stack3, thirdOutputChance, turns);
		}
		return true;
	}
}
