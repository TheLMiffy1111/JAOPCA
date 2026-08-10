package thelm.jaopca.compat.oritech.recipes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.RecipeContent;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final int time;

	public CentrifugeRecipeSerializer(Identifier key, Object input, Object output, int outputCount, int time) {
		this(key, input, output, outputCount, ItemStack.EMPTY, 0, time);
	}

	public CentrifugeRecipeSerializer(Identifier key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, outputCount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStackTemplate secondStack = MiscHelper.INSTANCE.getItemStackTemplate(secondOutput, secondOutputCount);
		List<ItemStackTemplate> results = secondStack == null ? List.of(stack) : List.of(stack, secondStack);
		OritechRecipe recipe = new OritechRecipe(List.of(ing), results, Optional.empty(), List.of(), time, RecipeContent.CENTRIFUGE.get());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
