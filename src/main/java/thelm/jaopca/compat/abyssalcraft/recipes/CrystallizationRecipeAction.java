package thelm.jaopca.compat.abyssalcraft.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class CrystallizationRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float experience;

	public CrystallizationRecipeAction(String key, Object input, Object output, int outputCount, float experience) {
		this(key, input, output, outputCount, null, 0, experience);
	}

	public CrystallizationRecipeAction(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float experience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.experience = experience;
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
		for(ItemStack in : ing) {
			AbyssalCraftAPI.addCrystallization(in.copy(), stack1, stack2, experience);
		}
		return true;
	}
}
