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
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class IngotFormerRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final int power;

	public IngotFormerRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, int time, int power) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.power = power;
	}

	@Override
	public JsonElement get() {
		SizedFluidIngredient ing = MiscHelper.INSTANCE.getSizedFluidIngredient(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		UniversalProcessorRecipe recipe = new UniversalProcessorRecipe(Processors.INGOT_FORMER, List.of(), List.of(ing), List.of(ItemOutput.of(stack.getItem(), stack.getCount())), List.of(), time, power);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
