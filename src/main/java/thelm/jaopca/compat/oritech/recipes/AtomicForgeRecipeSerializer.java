package thelm.jaopca.compat.oritech.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import dev.architectury.fluid.FluidStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.RecipeContent;
import rearth.oritech.util.FluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class AtomicForgeRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object secondInput;
	public final Object thirdInput;
	public final Object output;
	public final int outputCount;
	public final int time;

	public AtomicForgeRecipeSerializer(ResourceLocation key, Object input, Object secondInput, Object thirdInput, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.secondInput = secondInput;
		this.thirdInput = thirdInput;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Ingredient secondIng = MiscHelper.INSTANCE.getIngredient(secondInput);
		Ingredient thirdIng = MiscHelper.INSTANCE.getIngredient(thirdInput);
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		List<Ingredient> inputs = thirdIng == null ? secondIng == null ? List.of(ing) : List.of(ing, secondIng) : List.of(ing, secondIng, thirdIng);
		OritechRecipe recipe = new OritechRecipe(time, inputs, List.of(stack), RecipeContent.ATOMIC_FORGE, FluidIngredient.EMPTY, FluidStack.empty());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
