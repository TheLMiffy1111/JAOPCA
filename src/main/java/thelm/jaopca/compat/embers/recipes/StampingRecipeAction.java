package thelm.jaopca.compat.embers.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class StampingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object stamp;
	public final Object output;
	public final int outputCount;

	public StampingRecipeAction(ResourceLocation key, Object itemInput, Object stamp, Object output, int outputCount) {
		this(key, itemInput, null, 0, stamp, output, outputCount);
	}

	public StampingRecipeAction(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object stamp, Object output, int outputCount) {
		this(key, ItemStack.EMPTY, fluidInput, fluidInputAmount, stamp, output, outputCount);
	}

	public StampingRecipeAction(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object stamp, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.stamp = stamp;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public boolean register() {
		Ingredient itemIng = MiscHelper.INSTANCE.getIngredient(itemInput);
		FluidStack fluidIng = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
		if(itemIng == null && fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+itemInput+", "+fluidInput);
		}
		Ingredient stampIng = MiscHelper.INSTANCE.getIngredient(stamp);
		if(stampIng == null) {
			throw new IllegalArgumentException("Empty stamp in recipe "+key+": "+stamp);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		if(itemIng == null) {
			itemIng = Ingredient.EMPTY;
		}
		return RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(itemIng, fluidIng, stampIng, stack));
	}
}
