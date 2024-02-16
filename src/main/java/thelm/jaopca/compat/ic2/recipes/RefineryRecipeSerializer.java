package thelm.jaopca.compat.ic2.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ic2.api.recipes.RecipeRegistry;
import ic2.api.recipes.ingridients.inputs.IInput;
import ic2.api.recipes.ingridients.inputs.IngredientInput;
import ic2.api.recipes.ingridients.recipes.IFluidRecipeOutput;
import ic2.api.recipes.ingridients.recipes.RangeFluidOutput;
import ic2.api.recipes.misc.RecipeMods;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class RefineryRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object secondFluidInput;
	public final int secondFluidInputAmount;
	public final Object itemOutput;
	public final int itemOutputCountMin;
	public final int itemOutputCountMax;
	public final Object fluidOutput;
	public final int fluidOutputAmountMin;
	public final int fluidOutputAmountMax;
	public final double time;
	public final double energy;

	public RefineryRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object itemOutput, int itemOutputCountMin, int itemOutputCountMax, double time, double energy) {
		this(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, FluidStack.EMPTY, 0, itemOutput, itemOutputCountMin, itemOutputCountMax, FluidStack.EMPTY, 0, 0, time, energy);
	}

	public RefineryRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object secondFluidInput, int secondFluidInputAmount, Object itemOutput, int itemOutputCountMin, int itemOutputCountMax, Object fluidOutput, int fluidOutputAmountMin, int fluidOutputAmountMax, double time, double energy) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.secondFluidInput = secondFluidInput;
		this.secondFluidInputAmount = secondFluidInputAmount;
		this.itemOutput = itemOutput;
		this.itemOutputCountMin = itemOutputCountMin;
		this.itemOutputCountMax = itemOutputCountMax;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmountMin = fluidOutputAmountMin;
		this.fluidOutputAmountMax = fluidOutputAmountMax;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		FluidStack fluidIng1 = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
		FluidStack fluidIng2 = MiscHelper.INSTANCE.getFluidStack(secondFluidInput, secondFluidInputAmount);
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		if(fluidIng1.isEmpty() && fluidIng2.isEmpty() && ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+fluidInput+", "+secondFluidInput+", "+itemInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(itemOutput, 1);
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, 1);
		if(stack.isEmpty() && fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+itemOutput+", "+fluidOutput);
		}
		IInput input = new IngredientInput(ing, itemInputCount);
		CompoundTag mods = new CompoundTag();
		if(time != 1) {
			RecipeMods.RECIPE_TIME.create(mods, time);
		}
		if(energy != 1) {
			RecipeMods.ENERGY_USAGE.create(mods, energy);
		}
		IFluidRecipeOutput output = new RangeFluidOutput(stack, itemOutputCountMin, itemOutputCountMax, fluidStack, fluidOutputAmountMin, fluidOutputAmountMax, mods);

		JsonObject json = new JsonObject();
		json.addProperty("type", "ic2:refinery");
		json.add("input", RecipeRegistry.INGREDIENTS.serializeInput(input));
		json.add("first_tank", MiscHelper.INSTANCE.serializeFluidStack(fluidIng1));
		json.add("second_tank", MiscHelper.INSTANCE.serializeFluidStack(fluidIng2));
		json.add("output", RecipeRegistry.INGREDIENTS.serializeFluidOutput(output));

		return json;
	}
}
