package thelm.jaopca.compat.bcoreprocessing.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kotlin.TuplesKt;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.ndrei.bcoreprocessing.api.recipes.OreProcessingRecipes;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class OreProcessorRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputAmount;
	public final Object secondOutput;
	public final int secondOutputAmount;
	public final int time;

	public OreProcessorRecipeAction(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, Object secondOutput, int secondOutputAmount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.secondOutput = secondOutput;
		this.secondOutputAmount = secondOutputAmount;
		this.time = time;
	}

	@Override
	public boolean register() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack1 = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		FluidStack stack2 = MiscHelper.INSTANCE.getFluidStack(secondOutput, secondOutputAmount);
		if(stack1 == null && stack2 == null) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+output+", "+secondOutput);
		}
		for(ItemStack in : ing.getMatchingStacks()) {
			OreProcessingRecipes.INSTANCE.getOreProcessorRecipes().registerSimpleRecipe(
					MiscHelper.INSTANCE.resizeItemStack(in, inputCount), TuplesKt.to(stack1, stack2), inputCount);
		}
		return true;
	}
}
