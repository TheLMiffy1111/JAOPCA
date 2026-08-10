package thelm.jaopca.compat.oritech.recipes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.RecipeContent;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class RefineryRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputCount;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final Object secondFluidOutput;
	public final int secondFluidOutputAmount;
	public final Object thirdFluidOutput;
	public final int thirdFluidOutputAmount;
	public final int time;

	public RefineryRecipeSerializer(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time) {
		this(key, input, fluidInput, fluidInputAmount, output, outputCount, Fluids.EMPTY, 0, Fluids.EMPTY, 0, Fluids.EMPTY, 0, time);
	}

	public RefineryRecipeSerializer(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		this(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, Fluids.EMPTY, 0, Fluids.EMPTY, 0, time);
	}

	public RefineryRecipeSerializer(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, Object secondFluidOutput, int secondFluidOutputAmount, int time) {
		this(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, secondFluidOutput, secondFluidOutputAmount, Fluids.EMPTY, 0, time);
	}

	public RefineryRecipeSerializer(Identifier key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, Object secondFluidOutput, int secondFluidOutputAmount, Object thirdFluidOutput, int thirdFluidOutputAmount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.secondFluidOutput = secondFluidOutput;
		this.secondFluidOutputAmount = secondFluidOutputAmount;
		this.thirdFluidOutput = thirdFluidOutput;
		this.thirdFluidOutputAmount = thirdFluidOutputAmount;
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
		FluidStackTemplate fluidStack = MiscHelper.INSTANCE.getFluidStackTemplate(fluidOutput, fluidOutputAmount);
		if(stack == null && fluidStack == null) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+output+", "+fluidOutput);
		}
		FluidStackTemplate secondFluidStack = MiscHelper.INSTANCE.getFluidStackTemplate(secondFluidOutput, secondFluidOutputAmount);
		FluidStackTemplate thirdFluidStack = MiscHelper.INSTANCE.getFluidStackTemplate(thirdFluidOutput, thirdFluidOutputAmount);
		List<Ingredient> inputs = ing == null ? List.of() : List.of(ing);
		List<ItemStackTemplate> results = stack == null ? List.of() : List.of(stack);
		List<FluidStackTemplate> fluidResults = thirdFluidStack == null ? secondFluidStack == null ? fluidStack == null ? List.of() : List.of(fluidStack) : List.of(fluidStack, secondFluidStack) : List.of(fluidStack, secondFluidStack, thirdFluidStack);
		OritechRecipe recipe = new OritechRecipe(inputs, results, Optional.ofNullable(fluidIng), fluidResults, time, RecipeContent.REFINERY.get());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
