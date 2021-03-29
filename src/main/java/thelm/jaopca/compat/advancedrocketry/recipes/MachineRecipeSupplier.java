package thelm.jaopca.compat.advancedrocketry.recipes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.utils.MiscHelper;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;
import zmaster587.libVulpes.recipe.RecipesMachine;

public abstract class MachineRecipeSupplier implements Supplier<RecipesMachine.Recipe> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] itemInput;
	public final Object[] fluidInput;
	public final Object[] itemOutput;
	public final Object[] fluidOutput;
	public final int time;
	public final int energy;
	public final int maxOutput;

	public MachineRecipeSupplier(ResourceLocation key, Object[] itemInput, Object[] fluidInput, Object[] itemOutput, Object[] fluidOutput, int time, int energy, int maxOutput) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.time = time;
		this.energy = energy;
		this.maxOutput = maxOutput;
	}

	public abstract RecipeMachineFactory factoryInstance();

	@Override
	public RecipesMachine.Recipe get() {
		List<List<ItemStack>> itemInputs = new LinkedList<>();
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
			List<ItemStack> ingStacks = new LinkedList<>();
			for(ItemStack is : ing.getMatchingStacks()) {
				ItemStack copy = is.copy();
				copy.setCount(count);
				ingStacks.add(copy);
			}
			itemInputs.add(ingStacks);
		}
		List<FluidStack> fluidInputs = new LinkedList<>();
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
		List<RecipesMachine.ChanceItemStack> itemOutputs = new LinkedList<>();
		i = 0;
		while(i < itemOutput.length) {
			Object out = itemOutput[i];
			++i;
			Integer count = 1;
			if(i < itemOutput.length && itemOutput[i] instanceof Integer) {
				count = (Integer)itemOutput[i];
				++i;
			}
			Float chance = 0F;
			if(i < itemOutput.length && itemOutput[i] instanceof Float) {
				chance = (Float)itemOutput[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			itemOutputs.add(new RecipesMachine.ChanceItemStack(stack, chance));
		}
		List<RecipesMachine.ChanceFluidStack> fluidOutputs = new LinkedList<>();
		i = 0;
		while(i < fluidOutput.length) {
			Object out = fluidOutput[i];
			++i;
			Integer amount = 1;
			if(i < fluidOutput.length && fluidOutput[i] instanceof Integer) {
				amount = (Integer)fluidOutput[i];
				++i;
			}
			Float chance = 0F;
			if(i < fluidOutput.length && fluidOutput[i] instanceof Float) {
				chance = (Float)fluidOutput[i];
				++i;
			}
			FluidStack stack = MiscHelper.INSTANCE.getFluidStack(out, amount);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			fluidOutputs.add(new RecipesMachine.ChanceFluidStack(stack, chance));
		}
		RecipesMachine.Recipe rec = new RecipesMachine.Recipe(factoryInstance(), key, itemOutputs, itemInputs, fluidOutputs, fluidInputs, time, energy, Collections.emptyMap());
		rec.setMaxOutputSize(maxOutput);
		RecipesMachine.getInstance().recipeList.get(factoryInstance().getMachine()).add(rec);
		return rec;
	}
}
