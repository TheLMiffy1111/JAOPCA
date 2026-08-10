package thelm.jaopca.compat.oritech.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.RecipeContent;
import rearth.oritech.util.FluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.oritech.OritechHelper;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeFluidRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final int time;

	public CentrifugeFluidRecipeSerializer(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		this(key, input, fluidInput, fluidInputAmount, output, outputCount, ItemStack.EMPTY, 0, fluidOutput, fluidOutputAmount, time);
	}

	public CentrifugeFluidRecipeSerializer(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		FluidIngredient fluidIng = OritechHelper.INSTANCE.getFluidIngredient(fluidInput, fluidInputAmount);
		if(ing == null && fluidIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+input+", "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(stack.isEmpty() && secondStack.isEmpty() && fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+output+", "+secondOutput+", "+fluidOutput);
		}
		List<Ingredient> inputs = ing == null ? List.of() : List.of(ing);
		List<ItemStack> results = secondStack.isEmpty() ? stack.isEmpty() ? List.of() : List.of(stack) : List.of(stack, secondStack);
		OritechRecipe recipe = new OritechRecipe(time, inputs, results, RecipeContent.CENTRIFUGE_FLUID, fluidIng, FluidStackHooksForge.fromForge(fluidStack));
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
