package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class BlastingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final CookingBookCategory category;
	public final Object input;
	public final Object output;
	public final int count;
	public final float experience;
	public final int time;

	public BlastingRecipeSerializer(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		this(key, "", CookingBookCategory.MISC, input, output, count, experience, time);
	}

	public BlastingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		this(key, group, CookingBookCategory.MISC, input, output, count, experience, time);
	}

	public BlastingRecipeSerializer(ResourceLocation key, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		this(key, "", category, input, output, count, experience, time);
	}

	public BlastingRecipeSerializer(ResourceLocation key, String group, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.category = Objects.requireNonNull(category);
		this.input = input;
		this.output = output;
		this.count = count;
		this.experience = experience;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BlastingRecipe recipe = new BlastingRecipe(group, category, ing, stack, experience, time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
