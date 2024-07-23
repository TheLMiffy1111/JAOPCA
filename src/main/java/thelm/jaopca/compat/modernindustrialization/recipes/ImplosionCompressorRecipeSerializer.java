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
import aztech.modern_industrialization.thirdparty.fabrictransfer.api.item.ItemVariant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class ImplosionCompressorRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object[] output;
	public final int eu;
	public final int duration;

	public ImplosionCompressorRecipeSerializer(ResourceLocation key, Object[] input, Object[] output, int eu, int duration) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
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
		List<MachineRecipe.ItemOutput> outputs = new ArrayList<>();
		i = 0;
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
			outputs.add(new MachineRecipe.ItemOutput(ItemVariant.of(is), count, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		MachineRecipe recipe = new MIRecipeConstructor(MIMachineRecipeTypes.IMPLOSION_COMPRESSOR, eu, duration).recipe();
		recipe.itemInputs.addAll(inputs);
		recipe.itemOutputs.addAll(outputs);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
