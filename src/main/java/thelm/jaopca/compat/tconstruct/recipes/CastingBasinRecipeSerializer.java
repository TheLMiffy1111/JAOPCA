package thelm.jaopca.compat.tconstruct.recipes;

import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.tconstruct.TConstructHelper;
import thelm.jaopca.utils.MiscHelper;

public class CastingBasinRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object cast;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final ToIntFunction<FluidStack> time;
	public final boolean consumeCast;
	public final boolean switchSlots;

	public CastingBasinRecipeSerializer(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		this(key, "", cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots);
	}

	public CastingBasinRecipeSerializer(ResourceLocation key, String group, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.cast = cast;
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.consumeCast = consumeCast;
		this.switchSlots = switchSlots;
	}

	@Override
	public JsonElement get() {
		FluidIngredient fluidIng = TConstructHelper.INSTANCE.getFluidIngredient(input, inputAmount);
		if(fluidIng.getFluids().isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack funcStack = MiscHelper.INSTANCE.getFluidStack(input, inputAmount);
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(cast);
		ItemOutput out = TConstructHelper.INSTANCE.getItemOutput(output, outputCount);
		if(out.get().isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "tconstruct:casting_basin");
		if(!group.isEmpty()) {
			json.addProperty("group", group);
		}
		json.add("fluid", fluidIng.serialize());
		if(!ing.isEmpty()) {
			json.add("cast", ing.toJson());
			json.addProperty("cast_consumed", consumeCast);
		}
		json.add("result", out.serialize());
		json.addProperty("cooling_time", time.applyAsInt(funcStack));
		json.addProperty("switch_slots", switchSlots);

		return json;
	}
}
