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

public class GrinderRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final Object thirdOutput;
	public final int thirdOutputCount;
	public final int time;

	public GrinderRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int time) {
		this(key, input, output, outputCount, ItemStack.EMPTY, 0, ItemStack.EMPTY, 0, time);
	}

	public GrinderRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		this(key, input, output, outputCount, secondOutput, secondOutputCount, ItemStack.EMPTY, 0, time);
	}

	public GrinderRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object thirdOutput, int thirdOutputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.thirdOutput = thirdOutput;
		this.thirdOutputCount = thirdOutputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		ItemStack thirdStack = MiscHelper.INSTANCE.getItemStack(thirdOutput, thirdOutputCount);
		List<ItemStack> results = thirdStack.isEmpty() ? secondStack.isEmpty() ? List.of(stack) : List.of(stack, secondStack) : List.of(stack, secondStack, thirdStack);
		OritechRecipe recipe = new OritechRecipe(time, List.of(ing), results, RecipeContent.GRINDER, FluidIngredient.EMPTY, FluidStack.empty());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
