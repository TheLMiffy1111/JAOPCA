package thelm.jaopca.compat.nuclearcraft.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import igentuman.nc.recipe.ItemOutput;
import igentuman.nc.recipe.UniversalProcessorRecipe;
import igentuman.nc.setup.entries.Processors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class ManufactoryRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final int power;

	public ManufactoryRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		SizedIngredient ing = MiscHelper.INSTANCE.getSizedIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		UniversalProcessorRecipe recipe = new UniversalProcessorRecipe(Processors.MANUFACTORY, List.of(ing), List.of(), List.of(ItemOutput.of(stack.getItem(), stack.getCount())), List.of(), time, power);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
