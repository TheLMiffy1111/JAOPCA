package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import electrodynamics.common.recipe.categories.chemicalreactor.ChemicalReactorRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.ElectrodynamicsHelper;
import thelm.jaopca.utils.MiscHelper;
import voltaic.api.gas.GasStack;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.GasIngredient;
import voltaic.common.recipe.recipeutils.ProbableFluid;
import voltaic.common.recipe.recipeutils.ProbableGas;
import voltaic.common.recipe.recipeutils.ProbableItem;

public class ChemicalReactorRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object[] itemInput;
	public final Object[] fluidInput;
	public final Object[] gasInput;
	public final Object[] itemOutput;
	public final Object[] fluidOutput;
	public final Object[] gasOutput;
	public final double experience;
	public final int time;
	public final double energy;

	public ChemicalReactorRecipeSerializer(ResourceLocation key, Object[] itemInput, Object[] fluidInput, Object[] gasInput, Object[] itemOutput, Object[] fluidOutput, Object[] gasOutput, double experience, int time, double energy) {
		this(key, "", itemInput, fluidInput, gasInput, itemOutput, fluidOutput, gasOutput, experience, time, energy);
	}

	public ChemicalReactorRecipeSerializer(ResourceLocation key, String group, Object[] itemInput, Object[] fluidInput, Object[] gasInput, Object[] itemOutput, Object[] fluidOutput, Object[] gasOutput, double experience, int time, double energy) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.gasInput = gasInput;
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.gasOutput = gasOutput;
		this.experience = experience;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		List<CountableIngredient> itemIngs = new ArrayList<>();
		int i = 0;
		while(i < itemInput.length) {
			Object in = itemInput[i];
			++i;
			Integer count = 1;
			if(i < itemInput.length && itemInput[i] instanceof Integer) {
				count = (Integer)itemInput[i];
				++i;
			}
			CountableIngredient ing = ElectrodynamicsHelper.INSTANCE.getCountableIngredient(in, count);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			itemIngs.add(ing);
		}
		List<FluidIngredient> fluidIngs = new ArrayList<>();
		i = 0;
		while(i < fluidInput.length) {
			Object in = fluidInput[i];
			++i;
			Integer amount = 1000;
			if(i < fluidInput.length && fluidInput[i] instanceof Integer) {
				amount = (Integer)fluidInput[i];
				++i;
			}
			FluidIngredient ing = ElectrodynamicsHelper.INSTANCE.getFluidIngredient(in, amount);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			fluidIngs.add(ing);
		}
		List<GasIngredient> gasIngs = new ArrayList<>();
		i = 0;
		while(i < gasInput.length) {
			Object in = gasInput[i];
			++i;
			Integer amount = 1000;
			if(i < gasInput.length && gasInput[i] instanceof Integer) {
				amount = (Integer)gasInput[i];
				++i;
			}
			Integer temperature = 293;
			if(i < gasInput.length && gasInput[i] instanceof Integer) {
				temperature = (Integer)gasInput[i];
				++i;
			}
			Integer pressure = 1;
			if(i < gasInput.length && gasInput[i] instanceof Integer) {
				pressure = (Integer)gasInput[i];
				++i;
			}
			GasIngredient ing = ElectrodynamicsHelper.INSTANCE.getGasIngredient(in, amount, temperature, pressure);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			gasIngs.add(ing);
		}
		ItemStack itemStack = null;
		List<ProbableItem> itemByproducts = new ArrayList<>();
		i = 0;
		while(i < itemOutput.length) {
			Object out = itemOutput[i];
			++i;
			Integer count = 1;
			if(i < itemOutput.length && itemOutput[i] instanceof Integer) {
				count = (Integer)itemOutput[i];
				++i;
			}
			Float chance = 1F;
			if(i < itemOutput.length && itemOutput[i] instanceof Float) {
				chance = (Float)itemOutput[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(itemStack == null) {
				itemStack = stack;
				continue;
			}
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			itemByproducts.add(new ProbableItem(stack, chance));
		}
		if(itemStack == null) {
			itemStack = ItemStack.EMPTY;
		}
		FluidStack fluidStack = null;
		List<ProbableFluid> fluidByproducts = new ArrayList<>();
		i = 0;
		while(i < fluidOutput.length) {
			Object out = fluidOutput[i];
			++i;
			Integer amount = 1000;
			if(i < fluidOutput.length && fluidOutput[i] instanceof Integer) {
				amount = (Integer)fluidOutput[i];
				++i;
			}
			Float chance = 1F;
			if(i < fluidOutput.length && fluidOutput[i] instanceof Float) {
				chance = (Float)fluidOutput[i];
				++i;
			}
			FluidStack stack = MiscHelper.INSTANCE.getFluidStack(out, amount);
			if(fluidStack == null) {
				fluidStack = stack;
				continue;
			}
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			fluidByproducts.add(new ProbableFluid(stack, chance));
		}
		if(fluidStack == null) {
			fluidStack = FluidStack.EMPTY;
		}
		GasStack gasStack = null;
		List<ProbableGas> gasByproducts = new ArrayList<>();
		i = 0;
		while(i < gasOutput.length) {
			Object out = gasOutput[i];
			++i;
			Integer amount = 1000;
			if(i < gasOutput.length && gasOutput[i] instanceof Integer) {
				amount = (Integer)gasOutput[i];
				++i;
			}
			Float chance = 1F;
			if(i < gasOutput.length && gasOutput[i] instanceof Float) {
				chance = (Float)gasOutput[i];
				++i;
			}
			Integer temperature = 293;
			if(i < gasOutput.length && gasOutput[i] instanceof Integer) {
				temperature = (Integer)gasOutput[i];
				++i;
			}
			Integer pressure = 1;
			if(i < gasOutput.length && gasOutput[i] instanceof Integer) {
				pressure = (Integer)gasOutput[i];
				++i;
			}
			GasStack stack = ElectrodynamicsHelper.INSTANCE.getGasStack(out, amount, temperature, pressure);
			if(gasStack == null) {
				gasStack = stack;
				continue;
			}
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			gasByproducts.add(new ProbableGas(stack, chance));
		}
		if(gasStack == null) {
			gasStack = GasStack.EMPTY;
		}
		if(itemStack.isEmpty() && fluidStack.isEmpty() && gasStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(itemOutput)+", "+Arrays.deepToString(fluidOutput)+", "+Arrays.deepToString(gasOutput));
		}
		ChemicalReactorRecipe recipe = new ChemicalReactorRecipe(group, itemIngs, fluidIngs, gasIngs, itemStack, fluidStack, gasStack, experience, time, energy, itemByproducts, fluidByproducts, gasByproducts);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
