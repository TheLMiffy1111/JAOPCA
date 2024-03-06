package thelm.jaopca.compat.modernindustrialization.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public class UnpackerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final float inputChance;
	public final Object[] output;
	public final int eu;
	public final int duration;

	public UnpackerRecipeSerializer(ResourceLocation key, Object input, int inputCount, float inputChance, Object[] output, int eu, int duration) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.inputChance = inputChance;
		this.output = output;
		this.eu = eu;
		this.duration = duration;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<MachineRecipe.ItemOutput> outputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Float chance = 1F;
			if(i < output.length && output[i] instanceof Float) {
				chance = (Float)output[i];
				++i;
			}
			ItemStack is = MiscHelper.INSTANCE.getItemStack(out, count);
			if(is.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(new MachineRecipe.ItemOutput(is.getItem(), count, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		MachineRecipe recipe = new MIRecipeConstructor(MIMachineRecipeTypes.UNPACKER, eu, duration).recipe();
		recipe.itemInputs.add(new MachineRecipe.ItemInput(ing, inputCount, inputChance));
		recipe.itemOutputs.addAll(outputs);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
