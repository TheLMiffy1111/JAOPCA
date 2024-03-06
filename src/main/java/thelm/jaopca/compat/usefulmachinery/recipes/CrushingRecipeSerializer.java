package thelm.jaopca.compat.usefulmachinery.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.themcbrothers.usefulmachinery.core.MachineryItems;
import net.themcbrothers.usefulmachinery.recipe.CrushingRecipe;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CrushingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondOutputChance;
	public final int time;
	public final boolean supportsEfficiency;
	public final boolean supportsPrecision;

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int time, boolean supportsEfficiency, boolean supportsPrecision) {
		this(key, "", input, output, outputCount, ItemStack.EMPTY, 0, 0, time, supportsEfficiency, supportsPrecision);
	}

	public CrushingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int outputCount, int time, boolean supportsEfficiency, boolean supportsPrecision) {
		this(key, group, input, output, outputCount, ItemStack.EMPTY, 0, 0, time, supportsEfficiency, supportsPrecision);
	}

	public CrushingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int time, boolean supportsEfficiency, boolean supportsPrecision) {
		this(key, "", input, output, outputCount, secondOutput, secondOutputCount, secondOutputChance, time, supportsEfficiency, supportsPrecision);
	}

	public CrushingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondOutputChance, int time, boolean supportsEfficiency, boolean supportsPrecision) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondOutputChance = secondOutputChance;
		this.time = time;
		this.supportsEfficiency = supportsEfficiency;
		this.supportsPrecision = supportsPrecision;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack secondStack = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		List<Ingredient.Value> upgradeIngs = new ArrayList<>(2);
		if(supportsEfficiency) {
			upgradeIngs.add(new Ingredient.ItemValue(new ItemStack(MachineryItems.EFFICIENCY_UPGRADE.asItem())));
		}
		if(supportsPrecision) {
			upgradeIngs.add(new Ingredient.ItemValue(new ItemStack(MachineryItems.PRECISION_UPGRADE.asItem())));
		}
		Ingredient upgradeIng = Ingredient.fromValues(upgradeIngs.stream());
		CrushingRecipe recipe = new CrushingRecipe(group, ing, upgradeIng, stack, secondStack, secondOutputChance, time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
