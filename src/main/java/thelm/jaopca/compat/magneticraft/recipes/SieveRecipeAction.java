package thelm.jaopca.compat.magneticraft.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cout970.magneticraft.api.MagneticraftApi;
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipeManager;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class SieveRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final float outputChance;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondOutputChance;
	public final Object thirdOutput;
	public final int thirdOutputCount;
	public final float thirdOutputChance;
	public final float time;

	public SieveRecipeAction(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, float time) {
		this(key, input, output, outputCount, outputChance, ItemStack.EMPTY, 0, 0, ItemStack.EMPTY, 0, 0, time);
	}

	public SieveRecipeAction(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondOutputChance, float time) {
		this(key, input, output, outputCount, outputChance, secondOutput, secondOutputCount, secondOutputChance, ItemStack.EMPTY, 0, 0, time);
	}

	public SieveRecipeAction(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance, float time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondOutputChance = secondOutputChance;
		this.thirdOutput = thirdOutput;
		this.thirdOutputCount = thirdOutputCount;
		this.thirdOutputChance = thirdOutputChance;
		this.time = time;
	}

	@Override
	public boolean register() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack1 = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack1.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack stack2 = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		ItemStack stack3 = MiscHelper.INSTANCE.getItemStack(thirdOutput, thirdOutputCount);
		ISieveRecipeManager manager = MagneticraftApi.getSieveRecipeManager();
		for(ItemStack in : ing.getMatchingStacks()) {
			manager.registerRecipe(manager.createRecipe(in.copy(), stack1, outputChance, stack2, secondOutputChance, stack3, thirdOutputChance, time, false));
		}
		return true;
	}
}
