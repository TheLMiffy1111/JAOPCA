package thelm.jaopca.compat.actuallyadditions.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import de.ellpeck.actuallyadditions.mod.crafting.CrushingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final float outputChance;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondChance;

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, float outputChance) {
		this(key, input, output, outputCount, outputChance, ItemStack.EMPTY, 0, 0);
	}

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondChance) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondChance = secondChance;
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
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		CrushingRecipe recipe = new CrushingRecipe(ing, stack, outputChance, secondStack, secondChance);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
