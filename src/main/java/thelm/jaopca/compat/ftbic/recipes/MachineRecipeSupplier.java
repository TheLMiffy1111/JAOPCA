package thelm.jaopca.compat.ftbic.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.utils.MiscHelper;

public abstract class MachineRecipeSupplier implements Supplier<MachineRecipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] itemInput;
	public final Object[] fluidInput;
	public final Object[] itemOutput;
	public final Object[] fluidOutput;
	public final double time;

	public MachineRecipeSupplier(ResourceLocation key, Object[] itemInput, Object[] fluidInput, Object[] itemOutput, Object[] fluidOutput, double time) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.time = time;
	}

	public abstract Supplier<MachineRecipeSerializer> serializerSupplier();

	@Override
	public MachineRecipe get() {
		List<IngredientWithCount> itemInputs = new ArrayList<>();
		int i = 0;
		while(i < itemInput.length) {
			Object in = itemInput[i];
			++i;
			Integer count = 1;
			if(i < itemInput.length && itemInput[i] instanceof Integer) {
				count = (Integer)itemInput[i];
				++i;
			}
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing.hasNoMatchingItems()) {
				LOGGER.warn("Empty ingredient in recipe {}: {}", key, in);
			}
			itemInputs.add(new IngredientWithCount(ing, count));
		}
		List<FluidStack> fluidInputs = new ArrayList<>();
		i = 0;
		while(i < fluidInput.length) {
			Object in = fluidInput[i];
			++i;
			Integer amount = 1;
			if(i < fluidInput.length && fluidInput[i] instanceof Integer) {
				amount = (Integer)fluidInput[i];
				++i;
			}
			FluidStack fs = MiscHelper.INSTANCE.getFluidStack(in, amount);
			if(fs.isEmpty()) {
				LOGGER.warn("Empty input in recipe {}: {}", key, in);
			}
			fluidInputs.add(fs);
		}
		List<StackWithChance> itemOutputs = new ArrayList<>();
		i = 0;
		while(i < itemOutput.length) {
			Object out = itemOutput[i];
			++i;
			Integer count = 1;
			if(i < itemOutput.length && itemOutput[i] instanceof Integer) {
				count = (Integer)itemOutput[i];
				++i;
			}
			Double chance = 1D;
			if(i < itemOutput.length && itemOutput[i] instanceof Double) {
				chance = (Double)itemOutput[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			itemOutputs.add(new StackWithChance(stack, chance));
		}
		List<FluidStack> fluidOutputs = new ArrayList<>();
		i = 0;
		while(i < fluidOutput.length) {
			Object out = fluidOutput[i];
			++i;
			Integer amount = 1;
			if(i < fluidOutput.length && fluidOutput[i] instanceof Integer) {
				amount = (Integer)fluidOutput[i];
				++i;
			}
			FluidStack stack = MiscHelper.INSTANCE.getFluidStack(out, amount);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			fluidOutputs.add(stack);
		}
		MachineRecipe rec = new MachineRecipe(serializerSupplier().get(), key);
		rec.inputItems.addAll(itemInputs);
		rec.inputFluids.addAll(fluidInputs);
		rec.outputItems.addAll(itemOutputs);
		rec.outputFluids.addAll(fluidOutputs);
		rec.processingTime = time;
		return rec;
	}
}
