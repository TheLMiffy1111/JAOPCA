package thelm.jaopca.compat.oritech.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.RecipeContent;
import rearth.oritech.util.FluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.oritech.OritechHelper;
import thelm.jaopca.utils.MiscHelper;

public class RefineryRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
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

	public RefineryRecipeSerializer(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time) {
		this(key, input, fluidInput, fluidInputAmount, output, outputCount, Fluids.EMPTY, 0, Fluids.EMPTY, 0, Fluids.EMPTY, 0, time);
	}

	public RefineryRecipeSerializer(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, int time) {
		this(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, Fluids.EMPTY, 0, Fluids.EMPTY, 0, time);
	}

	public RefineryRecipeSerializer(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, Object secondFluidOutput, int secondFluidOutputAmount, int time) {
		this(key, input, fluidInput, fluidInputAmount, output, outputCount, fluidOutput, fluidOutputAmount, secondFluidOutput, secondFluidOutputAmount, Fluids.EMPTY, 0, time);
	}

	public RefineryRecipeSerializer(ResourceLocation key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, Object fluidOutput, int fluidOutputAmount, Object secondFluidOutput, int secondFluidOutputAmount, Object thirdFluidOutput, int thirdFluidOutputAmount, int time) {
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
		FluidIngredient fluidIng = OritechHelper.INSTANCE.getFluidIngredient(fluidInput, fluidInputAmount);
		if(ing == null && fluidIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+input+", "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(stack.isEmpty() && fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+output+", "+fluidOutput);
		}
		FluidStack secondFluidStack = MiscHelper.INSTANCE.getFluidStack(secondFluidOutput, secondFluidOutputAmount);
		FluidStack thirdFluidStack = MiscHelper.INSTANCE.getFluidStack(thirdFluidOutput, thirdFluidOutputAmount);
		List<ItemStack> results = stack.isEmpty() ? List.of() : List.of(stack);
		List<FluidStack> fluidResults = thirdFluidStack.isEmpty() ? secondFluidStack.isEmpty() ? fluidStack.isEmpty() ? List.of() : List.of(fluidStack) : List.of(fluidStack, secondFluidStack) : List.of(fluidStack, secondFluidStack, thirdFluidStack);
		OritechRecipe recipe = new OritechRecipe(time, List.of(ing), results, RecipeContent.REFINERY, fluidIng, Lists.transform(fluidResults, FluidStackHooksForge::fromForge));
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
