package thelm.jaopca.compat.electrodynamics.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import electrodynamics.common.recipe.categories.fluid2fluid.specificmachines.ElectrolosisChamberRecipe;
import electrodynamics.common.recipe.recipeutils.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.ElectrodynamicsHelper;
import thelm.jaopca.utils.MiscHelper;

public class ElectrolosisChamberRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputAmount;
	public final double experience;
	public final int time;
	public final double energy;

	public ElectrolosisChamberRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		this(key, "", input, inputAmount, output, outputAmount, experience, time, energy);
	}

	public ElectrolosisChamberRecipeSerializer(ResourceLocation key, String group, Object input, int inputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.experience = experience;
		this.time = time;
		this.energy = energy;
	}

	@Override
	public JsonElement get() {
		FluidIngredient ing = ElectrodynamicsHelper.INSTANCE.getFluidIngredient(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ElectrolosisChamberRecipe recipe = new ElectrolosisChamberRecipe(group, List.of(ing), stack, experience, time, energy, List.of(), List.of(), List.of());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
