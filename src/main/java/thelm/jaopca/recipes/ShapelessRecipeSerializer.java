package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe.CraftingBookInfo;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class ShapelessRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final String group;
	public final CraftingBookCategory category;
	public final Object output;
	public final int count;
	public final Object[] input;

	public ShapelessRecipeSerializer(Identifier key, Object output, int count, Object... input) {
		this(key, "", CraftingBookCategory.MISC, output, count, input);
	}

	public ShapelessRecipeSerializer(Identifier key, String group, Object output, int count, Object... input) {
		this(key, group, CraftingBookCategory.MISC, output, count, input);
	}

	public ShapelessRecipeSerializer(Identifier key, CraftingBookCategory category, Object output, int count, Object... input) {
		this(key, "", category, output, count, input);
	}

	public ShapelessRecipeSerializer(Identifier key, String group, CraftingBookCategory category, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.category = Objects.requireNonNull(category);
		this.output = output;
		this.count = count;
		this.input = Objects.requireNonNull(input);
	}

	@Override
	public JsonElement get() {
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, count);
		if(stack == null) {
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
		CommonInfo commonInfo = new CommonInfo(false);
		CraftingBookInfo bookInfo = new CraftingBookInfo(category, group);
		ShapelessRecipe recipe = new ShapelessRecipe(commonInfo, bookInfo, stack, ingredients);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
