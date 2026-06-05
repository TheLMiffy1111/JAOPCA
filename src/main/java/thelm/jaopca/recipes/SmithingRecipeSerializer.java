package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class SmithingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object template;
	public final Object base;
	public final Object addition;
	public final Object output;
	public final int count;

	public SmithingRecipeSerializer(Identifier key, Object template, Object base, Object addition, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.output = output;
		this.count = count;
	}

	@Override
	public JsonElement get() {
		Ingredient templateIng = MiscHelper.INSTANCE.getIngredient(template);
		Ingredient baseIng = MiscHelper.INSTANCE.getIngredient(base);
		if(baseIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+base);
		}
		Ingredient additionIng = MiscHelper.INSTANCE.getIngredient(addition);
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, count);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CommonInfo commonInfo = new CommonInfo(false);
		SmithingTransformRecipe recipe = new SmithingTransformRecipe(commonInfo, Optional.ofNullable(templateIng), baseIng, Optional.ofNullable(additionIng), stack);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
