package thelm.jaopca.compat.rotarycraft.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Reika.RotaryCraft.API.RecipeInterface;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class CrystallizerRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;

	public CrystallizerRecipeAction(String key, Object input, int inputAmount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public boolean register() {
		FluidStack ing = MiscHelper.INSTANCE.getFluidStack(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		RecipeInterface.crystallizer.addAPIRecipe(ing.getFluid(), inputAmount, stack);
		return true;
	}
}
