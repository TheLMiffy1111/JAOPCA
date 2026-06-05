package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe.CookingBookInfo;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CampfireCookingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final String group;
	public final CookingBookCategory category;
	public final Object input;
	public final Object output;
	public final int count;
	public final int time;

	public CampfireCookingRecipeSerializer(Identifier key, Object input, Object output, int count, int time) {
		this(key, "", CookingBookCategory.MISC, input, output, count, time);
	}

	public CampfireCookingRecipeSerializer(Identifier key, String group, Object input, Object output, int count, int time) {
		this(key, group, CookingBookCategory.MISC, input, output, count, time);
	}

	public CampfireCookingRecipeSerializer(Identifier key, CookingBookCategory category, Object input, Object output, int count, int time) {
		this(key, "", category, input, output, count, time);
	}

	public CampfireCookingRecipeSerializer(Identifier key, String group, CookingBookCategory category, Object input, Object output, int count, int time) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.category = Objects.requireNonNull(category);
		this.input = input;
		this.output = output;
		this.count = count;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, count);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CommonInfo commonInfo = new CommonInfo(false);
		CookingBookInfo bookInfo = new CookingBookInfo(category, group);
		CampfireCookingRecipe recipe = new CampfireCookingRecipe(commonInfo, bookInfo, ing, stack, 0, time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
