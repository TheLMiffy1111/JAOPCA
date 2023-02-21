package thelm.jaopca.compat.magneticraft.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cout970.magneticraft.api.access.MgRecipeRegister;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class CrusherRecipeAction implements IRecipeAction {

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

	public CrusherRecipeAction(String key, Object input, Object output, int outputCount) {
		this(key, input, output, outputCount, null, 0, 0, null, 0, 0);
	}

	public CrusherRecipeAction(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance) {
		this(key, input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, null, 0, 0);
	}

	public CrusherRecipeAction(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance) {
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
	}

	@Override
	public boolean register() {
		ItemStack ing = MiscHelper.INSTANCE.getItemStack(input, 1, true);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack1 = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack1 == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack stack2 = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount, false);
		ItemStack stack3 = MiscHelper.INSTANCE.getItemStack(thirdOutput, thirdOutputCount, false);
		MgRecipeRegister.registerCrusherRecipe(ing, stack1, stack2, secondOutputChance, stack3, thirdOutputChance);
		return true;
	}
}
