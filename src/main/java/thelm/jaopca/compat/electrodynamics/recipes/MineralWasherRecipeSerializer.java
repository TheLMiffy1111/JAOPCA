package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import electrodynamics.common.recipe.categories.fluiditem2fluid.specificmachines.MineralWasherRecipe;
import electrodynamics.common.recipe.recipeutils.CountableIngredient;
import electrodynamics.common.recipe.recipeutils.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.ElectrodynamicsHelper;
import thelm.jaopca.utils.MiscHelper;

public class MineralWasherRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputAmount;
	public final double experience;
	public final int time;
	public final double energy;

	public MineralWasherRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		this(key, "", itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputAmount, experience, time, energy);
	}

	public MineralWasherRecipeSerializer(ResourceLocation key, String group, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.experience = experience;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		CountableIngredient itemIng = ElectrodynamicsHelper.INSTANCE.getCountableIngredient(itemInput, itemInputCount);
		if(itemIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		FluidIngredient fluidIng = ElectrodynamicsHelper.INSTANCE.getFluidIngredient(fluidInput, fluidInputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		MineralWasherRecipe recipe = new MineralWasherRecipe(group, List.of(itemIng), List.of(fluidIng), stack, experience, time, energy, List.of(), List.of(), List.of());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
