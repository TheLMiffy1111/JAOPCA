package thelm.jaopca.compat.occultism.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.klikli_dev.occultism.crafting.recipe.CrushingRecipe;
import com.klikli_dev.occultism.crafting.recipe.result.RecipeResult;

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
	public final int minTier;
	public final int maxTier;
	public final int time;
	public final boolean ignoreMultiplier;

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int time, boolean ignoreMultiplier) {
		this(key, input, output, outputCount, -1, -1, time, ignoreMultiplier);
	}

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int minTier, int maxTier, int time, boolean ignoreMultiplier) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.minTier = minTier;
		this.maxTier = maxTier;
		this.time = time;
		this.ignoreMultiplier = ignoreMultiplier;
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
		CrushingRecipe recipe = new CrushingRecipe(ing, RecipeResult.of(stack), minTier, maxTier, time, ignoreMultiplier);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
