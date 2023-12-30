package thelm.jaopca.compat.embers.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputAmount;
	public final Object secondOutput;
	public final int secondOutputAmount;

	public MeltingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputAmount) {
		this(key, input, output, outputAmount, null, 0);
	}

	public MeltingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputAmount, Object secondOutput, int secondOutputAmount) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputAmount = outputAmount;
		this.secondOutput = secondOutput;
		this.secondOutputAmount = secondOutputAmount;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		FluidStack secondStack = MiscHelper.INSTANCE.getFluidStack(secondOutput, secondOutputAmount);

		JsonObject json = new JsonObject();
		json.addProperty("type", "embers:melting");
		json.add("input", ing.toJson());
		json.add("output", MiscHelper.INSTANCE.serializeFluidStack(stack));
		if(!secondStack.isEmpty()) {
			json.add("bonus", MiscHelper.INSTANCE.serializeFluidStack(secondStack));
		}

		return json;
	}
}
