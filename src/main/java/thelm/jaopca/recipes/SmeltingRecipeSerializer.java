package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class SmeltingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final CookingBookCategory category;
	public final Object input;
	public final Object output;
	public final int count;
	public final float experience;
	public final int time;

	public SmeltingRecipeSerializer(ResourceLocation key, Object input, Object output, int count, float experience, int time) {
		this(key, "", CookingBookCategory.MISC, input, output, count, experience, time);
	}

	public SmeltingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int count, float experience, int time) {
		this(key, group, CookingBookCategory.MISC, input, output, count, experience, time);
	}

	public SmeltingRecipeSerializer(ResourceLocation key, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
		this(key, "", category, input, output, count, experience, time);
	}

	public SmeltingRecipeSerializer(ResourceLocation key, String group, CookingBookCategory category, Object input, Object output, int count, float experience, int time) {
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
		SmeltingRecipe recipe = new SmeltingRecipe(group, category, ing, stack, experience, time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
