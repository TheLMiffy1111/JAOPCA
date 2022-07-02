package thelm.jaopca.compat.embers.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import teamroots.embers.recipe.ItemMeltingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputAmount;
	public final Object secondOutput;
	public final int secondOutputAmount;

	public MeltingRecipeAction(ResourceLocation key, Object input, Object output, int outputAmount) {
		this(key, input, output, outputAmount, null, 0);
	}

	public MeltingRecipeAction(ResourceLocation key, Object input, Object output, int outputAmount, Object secondOutput, int secondOutputAmount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputAmount = outputAmount;
		this.secondOutput = secondOutput;
		this.secondOutputAmount = secondOutputAmount;
	}

	@Override
	public boolean register() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		FluidStack stack2 = MiscHelper.INSTANCE.getFluidStack(secondOutput, secondOutputAmount);
		return RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(ing, stack).addBonusOutput(stack2));
	}
}
