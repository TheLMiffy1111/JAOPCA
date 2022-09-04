package thelm.jaopca.compat.factorium.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class GrinderRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final float outputChance;
	public final Object secondOutput;
	public final int secondOutputCount;
	public final float secondOutputChance;
	public final Object thirdOutput;
	public final int thirdOutputCount;
	public final float thirdOutputChance;

	public GrinderRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, float outputChance) {
		this(key, input, output, outputCount, outputChance, ItemStack.EMPTY, 0, 0, ItemStack.EMPTY, 0, 0);
	}

	public GrinderRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, float outputChance, Object secondOutput, int secondOutputCount, float secondOutputChance, Object thirdOutput, int thirdOutputCount, float thirdOutputChance) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.secondOutput = secondOutput;
		this.secondOutputCount = secondOutputCount;
		this.secondOutputChance = secondOutputChance;
		this.thirdOutput = thirdOutput;
		this.thirdOutputCount = thirdOutputCount;
		this.thirdOutputChance = thirdOutputChance;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack1 = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack1.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		ItemStack stack2 = MiscHelper.INSTANCE.getItemStack(secondOutput, secondOutputCount);
		ItemStack stack3 = MiscHelper.INSTANCE.getItemStack(thirdOutput, thirdOutputCount);

		JsonObject json = new JsonObject();
		json.addProperty("type", "factorium:grinder");
		json.add("input", ing.toJson());
		JsonObject outputJson1 = MiscHelper.INSTANCE.serializeItemStack(stack1);
		outputJson1.addProperty("chance", outputChance);
		json.add("output", outputJson1);
		if(!stack2.isEmpty()) {
			JsonObject outputJson2 = MiscHelper.INSTANCE.serializeItemStack(stack2);
			outputJson2.addProperty("chance", secondOutputChance);
			json.add("secondary", outputJson2);
		}
		if(!stack3.isEmpty()) {
			JsonObject outputJson3 = MiscHelper.INSTANCE.serializeItemStack(stack3);
			outputJson3.addProperty("chance", thirdOutputChance);
			json.add("bonus", outputJson3);
		}

		return json;
	}
}
