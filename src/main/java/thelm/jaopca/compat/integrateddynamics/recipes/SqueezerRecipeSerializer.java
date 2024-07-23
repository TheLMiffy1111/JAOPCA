package thelm.jaopca.compat.integrateddynamics.recipes;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyclops.integrateddynamics.core.recipe.type.RecipeSqueezer;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class SqueezerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object[] itemOutput;
	public final Object fluidOutput;
	public final int fluidOutputAmount;

	public SqueezerRecipeSerializer(ResourceLocation key, Object input, Object[] itemOutput) {
		this(key, input, itemOutput, Fluids.EMPTY, 0);
	}

	public SqueezerRecipeSerializer(ResourceLocation key, Object input, Object[] itemOutput, Object fluidOutput, int fluidOutputAmount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.itemOutput = itemOutput;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			itemResults.add(new RecipeSqueezer.IngredientChance(Either.left(Pair.of(stack, chance))));
		}
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(itemResults.isEmpty() && fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(itemOutput)+", "+fluidOutput);
		}
		RecipeSqueezer recipe = new RecipeSqueezer(ing, itemResults, fluidStack.isEmpty() ? Optional.empty() : Optional.of(fluidStack));
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
