package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class ShapelessRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final CraftingBookCategory category;
	public final Object output;
	public final int count;
	public final Object[] input;

	public ShapelessRecipeSerializer(ResourceLocation key, Object output, int count, Object... input) {
		this(key, "", CraftingBookCategory.MISC, output, count, input);
	}

	public ShapelessRecipeSerializer(ResourceLocation key, String group, Object output, int count, Object... input) {
		this(key, group, CraftingBookCategory.MISC, output, count, input);
	}

	public ShapelessRecipeSerializer(ResourceLocation key, CraftingBookCategory category, Object output, int count, Object... input) {
		this(key, "", category, output, count, input);
	}

	public ShapelessRecipeSerializer(ResourceLocation key, String group, CraftingBookCategory category, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.category = Objects.requireNonNull(category);
		this.output = output;
		this.count = count;
		this.input = Objects.requireNonNull(input);
	}

	@Override
	public JsonElement get() {
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for(Object in : input) {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			else {
				ingredients.add(ing);
			}
		}
		ShapelessRecipe recipe = new ShapelessRecipe(group, category, stack, ingredients);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
