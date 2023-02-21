package thelm.jaopca.compat.magneticraft.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cout970.magneticraft.api.access.MgRecipeRegister;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class HammerTableRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object output;
	public final int outputCount;

	public HammerTableRecipeAction(String key, Object input, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public boolean register() {
		ItemStack ing = MiscHelper.INSTANCE.getItemStack(input, 1, true);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		MgRecipeRegister.registerHammerTableRecipe(ing, stack);
		return true;
	}
}
