package thelm.jaopca.compat.hephaestus.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.titammods.setup.ModRecipes.CastingBasinRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CastingBasinRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object cast;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final int time;

	public CastingBasinRecipeSerializer(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.cast = cast;
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		SizedFluidIngredient fluidIng = MiscHelper.INSTANCE.getSizedFluidIngredient(input, inputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack funcStack = MiscHelper.INSTANCE.getFluidStack(input, inputAmount);
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(cast);
		ItemStack out = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(out == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CastingBasinRecipe recipe = new CastingBasinRecipe(ing == null ? Ingredient.EMPTY : ing, funcStack, BuiltInRegistries.ITEM.getKey(out.getItem()), out.getCount(), time);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
