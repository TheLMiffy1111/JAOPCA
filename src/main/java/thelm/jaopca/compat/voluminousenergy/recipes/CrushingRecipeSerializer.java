package thelm.jaopca.compat.voluminousenergy.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.veteam.voluminousenergy.recipe.CrusherRecipe;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.RegistryIngredient;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.VEChancedItemWithCount;
import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.VERecipeExperience;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.voluminousenergy.VoluminousEnergyHelper;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondChance;
	public final int time;
	public final int minExperience;
	public final int maxExperience;

	public CrushingRecipeSerializer(Identifier key, Object input, int inputCount, Object output, int outputCount, int time, int minExperience, int maxExperience) {
		this(key, input, inputCount, output, outputCount, ItemStack.EMPTY, 0, 0, time, minExperience, maxExperience);
	}

	public CrushingRecipeSerializer(Identifier key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time, int minExperience, int maxExperience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
		this.time = time;
		this.minExperience = minExperience;
		this.maxExperience = maxExperience;
	}

	@Override
	public JsonElement get() {
		RegistryIngredient ing = VoluminousEnergyHelper.INSTANCE.getRegistryIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, outputCount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStackTemplate secondStack = MiscHelper.INSTANCE.getItemStackTemplate(secondOutput, secondOutputCount);
		List<VEChancedItemWithCount> results = new ArrayList<>();
		results.add(new VEChancedItemWithCount(stack.item(), stack.count(), 1F));
		if(secondStack != null) {
			results.add(new VEChancedItemWithCount(secondStack.item(), secondStack.count(), secondChance));
		}
		CrusherRecipe recipe = new CrusherRecipe(List.of(ing), results, time, new VERecipeExperience(minExperience, maxExperience));
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
