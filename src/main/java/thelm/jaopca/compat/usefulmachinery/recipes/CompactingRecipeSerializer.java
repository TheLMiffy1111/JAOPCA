package thelm.jaopca.compat.usefulmachinery.recipes;

import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.themcbrothers.usefulmachinery.machine.CompactorMode;
import net.themcbrothers.usefulmachinery.recipe.CompactingRecipe;
import net.themcbrothers.usefulmachinery.recipe.ingredient.CountIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CompactingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final CompactorMode mode;

	public CompactingRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, String mode) {
		this(key, "", input, inputCount, output, outputCount, time, mode);
	}

	public CompactingRecipeSerializer(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, int time, String mode) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.mode = Arrays.stream(CompactorMode.values()).filter(m->m.name().equalsIgnoreCase(mode)).findAny().orElse(CompactorMode.PLATE);
	}

	@Override
	public JsonElement get() {
		Ingredient ing;
		if(input instanceof TagKey<?>) {
			ing = CountIngredient.of(inputCount, (TagKey<Item>)input);
		}
		else if(input instanceof String) {
			ing = CountIngredient.of(inputCount, MiscHelper.INSTANCE.getItemTagKey(new ResourceLocation((String)input)));
		}
		else if(input instanceof ResourceLocation) {
			ing = CountIngredient.of(inputCount, MiscHelper.INSTANCE.getItemTagKey((ResourceLocation)input));
		}
		else {
			LOGGER.warn("Non-tag ingredient in recipe {}", key);
			ing = MiscHelper.INSTANCE.getIngredient(input);
		}
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CompactingRecipe recipe = new CompactingRecipe(group, ing, stack, time, mode);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
