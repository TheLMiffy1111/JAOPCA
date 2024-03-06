package thelm.jaopca.compat.theurgy.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.klikli_dev.theurgy.TheurgyConstants;
import com.klikli_dev.theurgy.content.recipe.LiquefactionRecipe;
import com.klikli_dev.theurgy.content.recipe.ingredient.FluidIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.theurgy.TheurgyHelper;
import thelm.jaopca.utils.MiscHelper;

public class LiquefactionRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputCount;
	public final int time;

	public LiquefactionRecipeSerializer(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
		}
		FluidIngredient fluidIng = TheurgyHelper.INSTANCE.getFluidIngredient(fluidInput);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidIng);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CompoundTag nbt = new CompoundTag();
		if(itemInput instanceof String tag) {
			nbt.putString(TheurgyConstants.Nbt.SULFUR_SOURCE_ID, "#"+tag);
		}
		else if(itemInput instanceof TagKey<?> tag) {
			nbt.putString(TheurgyConstants.Nbt.SULFUR_SOURCE_ID, "#"+tag.location());
		}
		else {
			ItemStack inputStack = MiscHelper.INSTANCE.getItemStack(itemInput, 1);
			nbt.putString(TheurgyConstants.Nbt.SULFUR_SOURCE_ID, BuiltInRegistries.ITEM.getKey(inputStack.getItem()).toString());
		}
		stack.setTag(nbt);
		LiquefactionRecipe recipe = new LiquefactionRecipe(ing, fluidIng, fluidInputAmount, stack, time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
