package thelm.jaopca.compat.oritech.recipes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.RecipeContent;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeFluidRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
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

	public CentrifugeFluidRecipeSerializer(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		this(key, input, fluidInput, fluidInputAmount, output, outputCount, ItemStack.EMPTY, 0, fluidOutput, fluidOutputAmount, time);
	}

	public CentrifugeFluidRecipeSerializer(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object secondOutput, int secondOutputCount, Object fluidOutput, int fluidOutputAmount, int time) {
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
		SizedFluidIngredient fluidIng = MiscHelper.INSTANCE.getSizedFluidIngredient(fluidInput, fluidInputAmount);
		if(ing == null && fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+input+", "+fluidInput);
		}
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, outputCount);
		ItemStackTemplate secondStack = MiscHelper.INSTANCE.getItemStackTemplate(secondOutput, secondOutputCount);
		FluidStackTemplate fluidStack = MiscHelper.INSTANCE.getFluidStackTemplate(fluidOutput, fluidOutputAmount);
		if(stack == null && secondStack == null && fluidStack == null) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+output+", "+secondOutput+", "+fluidOutput);
		}
		List<Ingredient> inputs = ing == null ? List.of() : List.of(ing);
		List<ItemStackTemplate> results = secondStack == null ? stack == null ? List.of() : List.of(stack) : List.of(stack, secondStack);
		List<FluidStackTemplate> fluidResults = fluidStack == null ? List.of() : List.of(fluidStack);
		OritechRecipe recipe = new OritechRecipe(inputs, results, Optional.ofNullable(fluidIng), fluidResults, time, RecipeContent.CENTRIFUGE_FLUID.get());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
