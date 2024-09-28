package thelm.jaopca.compat.enderio.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.enderio.machines.common.recipe.SagMillingRecipe;
import com.enderio.machines.common.recipe.SagMillingRecipe.BonusType;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class SagMillingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int energy;
	public final BonusType bonusType;
	public final Object[] output;

	public SagMillingRecipeSerializer(ResourceLocation key, Object input, int energy, String bonusType, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.energy = energy;
		this.bonusType = Objects.requireNonNull(BonusType.valueOf(bonusType.toUpperCase(Locale.US)));
		this.output = output;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<SagMillingRecipe.OutputItem> outputs = new ArrayList<>();
		int i = 0;
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(new SagMillingRecipe.OutputItem(Either.left(stack.getItem()), count, chance, false));
		}
		SagMillingRecipe recipe = new SagMillingRecipe(ing, outputs, energy, bonusType);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
