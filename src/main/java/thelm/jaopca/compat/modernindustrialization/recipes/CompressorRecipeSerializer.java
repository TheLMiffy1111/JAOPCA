package thelm.jaopca.compat.modernindustrialization.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CompressorRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final float inputChance;
	public final Object output;
	public final int outputCount;
	public final float outputChance;
	public final int eu;
	public final int duration;

	public CompressorRecipeSerializer(ResourceLocation key, Object input, int inputCount, float inputChance, Object output, int outputCount, float outputChance, int eu, int duration) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.inputChance = inputChance;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.eu = eu;
		this.duration = duration;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		MachineRecipe recipe = new MIRecipeConstructor(MIMachineRecipeTypes.COMPRESSOR, eu, duration).recipe();
		recipe.itemInputs.add(new MachineRecipe.ItemInput(ing, inputCount, inputChance));
		recipe.itemOutputs.add(new MachineRecipe.ItemOutput(stack.getItem(), outputCount, outputChance));
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
