package thelm.jaopca.compat.randomadditions.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.randomadditions.common.systems.machine.MachineRecipe;
import com.creativemd.randomadditions.common.systems.machine.SubSystemMachine;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.randomadditions.RandomAdditionsHelper;
import thelm.jaopca.utils.MiscHelper;

public class CrusherRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int power;

	public CrusherRecipeAction(String key, Object input, int inputCount, Object output, int outputCount, int power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.power = power;
	}

	@Override
	public boolean register() {
		StackInfo ing = RandomAdditionsHelper.INSTANCE.getStackInfo(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		SubSystemMachine.instance.crusher.registerRecipe(new MachineRecipe(ing, stack, power));
		return true;
	}
}
