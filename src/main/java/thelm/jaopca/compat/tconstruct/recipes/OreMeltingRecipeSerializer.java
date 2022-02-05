package thelm.jaopca.compat.tconstruct.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class OreMeltingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int outputAmount;
	public final Object[] byproducts;
	public final int rate;
	public final ToIntFunction<FluidStack> temperature;
	public final ToIntFunction<FluidStack> time;

	public OreMeltingRecipeSerializer(ResourceLocation key, Object input, Object output, int outputAmount, int rate, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, Object... byproducts) {
		this(key, "", input, output, outputAmount, rate, temperature, time, byproducts);
	}

	public OreMeltingRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int outputAmount, int rate, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, Object... byproducts) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.outputAmount = outputAmount;
		this.byproducts = byproducts;
		this.rate = rate;
		this.temperature = temperature;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, outputAmount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		List<FluidStack> bys = new ArrayList<>();
		int i = 0;
		while(i < byproducts.length) {
			Object out = byproducts[i];
			++i;
			Integer amount = 30;
			if(i < byproducts.length && byproducts[i] instanceof Integer) {
				amount = (Integer)byproducts[i];
				++i;
			}
			FluidStack by = MiscHelper.INSTANCE.getFluidStack(out, amount);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			bys.add(by);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "tconstruct:ore_melting");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		json.add("ingredient", ing.toJson());
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("fluid", stack.getFluid().getRegistryName().toString());
		resultJson.addProperty("amount", stack.getAmount());
		json.add("result", resultJson);
		if(!bys.isEmpty()) {
			JsonArray byproductsJson = new JsonArray();
			for(FluidStack by : bys) {
				JsonObject byproductJson = new JsonObject();
				byproductJson.addProperty("fluid", by.getFluid().getRegistryName().toString());
				byproductJson.addProperty("amount", by.getAmount());
				byproductsJson.add(byproductJson);
			}
			json.add("byproducts", byproductsJson);
		}
		json.addProperty("temperature", temperature.applyAsInt(stack));
		json.addProperty("time", time.applyAsInt(stack));
		json.addProperty("rate", IMeltingContainer.OreRateType.values()[rate].getName());

		return json;
	}
}
