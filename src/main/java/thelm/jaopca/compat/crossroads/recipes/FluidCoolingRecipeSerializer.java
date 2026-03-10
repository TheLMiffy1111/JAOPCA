package thelm.jaopca.compat.crossroads.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.crossroads.CrossroadsHelper;
import thelm.jaopca.utils.MiscHelper;

public class FluidCoolingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final float maxTemp;
	public final float addedHeat;

	public FluidCoolingRecipeSerializer(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		this(key, "", input, inputAmount, output, outputCount, maxTemp, addedHeat);
	}

	public FluidCoolingRecipeSerializer(ResourceLocation key, String group, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.maxTemp = maxTemp;
		this.addedHeat = addedHeat;
	}

	@Override
	public JsonElement get() {
		FluidIngredient ing = CrossroadsHelper.INSTANCE.getFluidIngredient(input);
		if(ing == null || inputAmount <= 0) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		FluidCoolingRec recipe = new FluidCoolingRec(group, ing, inputAmount, stack, maxTemp, addedHeat);
		JsonObject json = MiscHelper.INSTANCE.serialize(FluidCoolingRec.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "crossroads:fluid_cooling");
		return json;
	}

	public record FluidCoolingRec(String group, FluidIngredient input, int inputAmount, ItemStack output, float maxTemp, float addedHeat) {
		public static final Codec<FluidCoolingRec> CODEC = RecordCodecBuilder.create(instance->instance.group(
				CraftingUtil.recipeGroupFieldCodec().forGetter(FluidCoolingRec::group),
				FluidIngredient.CODEC.fieldOf("input").forGetter(FluidCoolingRec::input),
				ExtraCodecs.POSITIVE_INT.fieldOf("fluid_amount").forGetter(FluidCoolingRec::inputAmount),
				ItemStack.OPTIONAL_CODEC.fieldOf("output").forGetter(FluidCoolingRec::output),
				Codec.FLOAT.fieldOf("max_temp").forGetter(FluidCoolingRec::maxTemp),
				Codec.FLOAT.optionalFieldOf("temp_change", 0F).forGetter(FluidCoolingRec::addedHeat)).
				apply(instance, FluidCoolingRec::new));
	}
}
