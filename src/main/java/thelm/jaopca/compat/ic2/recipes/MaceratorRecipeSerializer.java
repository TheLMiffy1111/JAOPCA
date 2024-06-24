package thelm.jaopca.compat.ic2.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ic2.api.recipes.RecipeRegistry;
import ic2.api.recipes.ingridients.generators.ItemGenerator;
import ic2.api.recipes.ingridients.inputs.IInput;
import ic2.api.recipes.ingridients.inputs.IngredientInput;
import ic2.api.recipes.ingridients.recipes.IRecipeOutput;
import ic2.api.recipes.ingridients.recipes.SimpleRecipeOutput;
import ic2.api.recipes.misc.RecipeMods;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class MaceratorRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final double time;
	public final double energy;
	public final float experience;

	public MaceratorRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double time, double energy, float experience) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.energy = energy;
		this.experience = experience;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		IInput input = new IngredientInput(ing, inputCount);
		CompoundTag mods = new CompoundTag();
		if(time != 1) {
			RecipeMods.RECIPE_TIME.create(mods, time);
		}
		if(energy != 1) {
			RecipeMods.ENERGY_USAGE.create(mods, energy);
		}
		IRecipeOutput output = new SimpleRecipeOutput(List.of(new ItemGenerator(stack.getItem(), stack.getCount())), mods, experience);

		JsonObject json = new JsonObject();
		json.addProperty("type", "ic2:macerator");
		json.add("input", RecipeRegistry.INGREDIENTS.serializeInput(input));
		json.add("output", RecipeRegistry.INGREDIENTS.serializeOutput(output));

		return json;
	}
}
