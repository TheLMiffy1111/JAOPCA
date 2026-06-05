package thelm.jaopca.compat.integrateddynamics.recipes;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyclops.integrateddynamics.core.recipe.type.RecipeMechanicalSqueezer;
import org.cyclops.integrateddynamics.core.recipe.type.RecipeSqueezer;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class MechanicalSqueezerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final Object[] itemOutput;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final int time;

	public MechanicalSqueezerRecipeSerializer(Identifier key, Object input, Object[] itemOutput, int time) {
		this(key, input, itemOutput, Fluids.EMPTY, 0, time);
	}

	public MechanicalSqueezerRecipeSerializer(Identifier key, Object input, Object[] itemOutput, Object fluidOutput, int fluidOutputAmount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		NonNullList<RecipeSqueezer.IngredientChance> itemResults = NonNullList.create();
		int i = 0;
		while(i < itemOutput.length) {
			Object out = itemOutput[i];
			++i;
			Integer count = 1;
			if(i < itemOutput.length && itemOutput[i] instanceof Integer) {
				count = (Integer)itemOutput[i];
				++i;
			}
			Float chance = 1F;
			if(i < itemOutput.length && itemOutput[i] instanceof Float) {
				chance = (Float)itemOutput[i];
				++i;
			}
			ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(out, count);
			if(stack == null) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			itemResults.add(new RecipeSqueezer.IngredientChance(Either.left(Pair.of(stack, chance))));
		}
		FluidStackTemplate fluidStack = MiscHelper.INSTANCE.getFluidStackTemplate(fluidOutput, fluidOutputAmount);
		if(itemResults.isEmpty() && fluidStack == null) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(itemOutput)+", "+fluidOutput);
		}
		RecipeMechanicalSqueezer recipe = new RecipeMechanicalSqueezer(ing, itemResults, Optional.ofNullable(fluidStack), time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
