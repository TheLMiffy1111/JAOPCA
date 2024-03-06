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

public class PackerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object output;
	public final int outputCount;
	public final float outputChance;
	public final int eu;
	public final int duration;

	public PackerRecipeSerializer(ResourceLocation key, Object[] input, Object output, int outputCount, float outputChance, int eu, int duration) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.eu = eu;
		this.duration = duration;
	}

	@Override
	public JsonElement get() {
		List<MachineRecipe.ItemInput> inputs = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer count = 1;
			if(i < input.length && input[i] instanceof Integer) {
				count = (Integer)input[i];
				++i;
			}
			Float chance = 1F;
			if(i < input.length && input[i] instanceof Float) {
				chance = (Float)input[i];
				++i;
			}
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			inputs.add(new MachineRecipe.ItemInput(ing, count, chance));
		}
		if(inputs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		MachineRecipe recipe = new MIRecipeConstructor(MIMachineRecipeTypes.PACKER, eu, duration).recipe();
		recipe.itemInputs.addAll(inputs);
		recipe.itemOutputs.add(new MachineRecipe.ItemOutput(stack.getItem(), outputCount, outputChance));
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
