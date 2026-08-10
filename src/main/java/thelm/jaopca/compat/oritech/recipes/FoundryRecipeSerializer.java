package thelm.jaopca.compat.oritech.recipes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import rearth.oritech.init.recipes.OritechRecipe;
import rearth.oritech.init.recipes.RecipeContent;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class FoundryRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final Object secondInput;
	public final Object output;
	public final int outputCount;
	public final int time;

	public FoundryRecipeSerializer(Identifier key, Object input, Object secondInput, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.secondInput = secondInput;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+input);
		}
		Ingredient secondIng = MiscHelper.INSTANCE.getIngredient(secondInput);
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, outputCount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		List<Ingredient> inputs = secondIng == null ? List.of(ing) : List.of(ing, secondIng);
		OritechRecipe recipe = new OritechRecipe(inputs, List.of(stack), Optional.empty(), List.of(), time, RecipeContent.FOUNDRY.get());
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
