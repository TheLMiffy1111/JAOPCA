package thelm.jaopca.compat.neovitae.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class AlchemyTableRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] input;
	public final Object output;
	public final int count;
	public final int cost;
	public final int time;
	public final int minTier;

	public AlchemyTableRecipeSerializer(ResourceLocation key, Object[] input, Object output, int count, int cost, int time, int minTier) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
		this.cost = cost;
		this.time = time;
		this.minTier = minTier;
	}

	@Override
	public JsonElement get() {
		List<Ingredient> inputList = new ArrayList<Ingredient>();
		for(Object in : input) {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			inputList.add(ing);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		TabulaVitaeRecipe recipe = new TabulaVitaeRecipe(inputList, stack, cost, time, minTier);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
