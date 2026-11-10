package thelm.jaopca.compat.hephaestus.recipes;

import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.titammods.setup.ModFluids;
import com.titammods.setup.ModRecipes.MeltingRecipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final Object fuel;
	public final int fuelAmount;
	public final Object output;
	public final int outputAmount;
	public final ToIntFunction<FluidStackTemplate> temperature;
	public final int time;

	public MeltingRecipeSerializer(Identifier key, Object input, Object fuel, int fuelAmount, Object output, int outputAmount, ToIntFunction<FluidStackTemplate> temperature, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.fuel = fuel;
		this.fuelAmount = fuelAmount;
		this.output = output;
		this.outputAmount = outputAmount;
		this.temperature = temperature;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStackTemplate stack = MiscHelper.INSTANCE.getFluidStackTemplate(output, outputAmount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		FluidStackTemplate fuelStack;
		if(fuel == null) {
			int temp = temperature.applyAsInt(stack);
			fuelStack = temp <= 1000 ? new FluidStackTemplate(Fluids.LAVA, fuelAmount) : new FluidStackTemplate(ModFluids.MOLTEN_BLAZE.source.get(), fuelAmount);
		}
		else {
			fuelStack = MiscHelper.INSTANCE.getFluidStackTemplate(fuel, fuelAmount);
			if(fuelStack == null) {
				throw new IllegalArgumentException("Empty input in recipe "+key+": "+fuel);
			}
		}
		MeltingRecipe recipe = new MeltingRecipe(
				ing,
				stack.fluid().getKey().identifier(), stack.amount(),
				fuelStack.fluid().getKey().identifier(), fuelStack.amount(),
				temperature.applyAsInt(stack),
				time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
