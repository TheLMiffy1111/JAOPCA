package thelm.jaopca.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class SmithingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object template;
	public final Object base;
	public final Object addition;
	public final Object output;
	public final int count;

	public SmithingRecipeSerializer(ResourceLocation key, Object template, Object base, Object addition, Object output, int count) {
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
		if(templateIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+template);
		}
		Ingredient baseIng = MiscHelper.INSTANCE.getIngredient(base);
		if(baseIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+base);
		}
		Ingredient additionIng = MiscHelper.INSTANCE.getIngredient(addition);
		if(additionIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+addition);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		SmithingTransformRecipe recipe = new SmithingTransformRecipe(templateIng, baseIng, additionIng, stack);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
