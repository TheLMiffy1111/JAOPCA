package thelm.jaopca.compat.crossroads.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class BlastFurnaceRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int amount;
	public final int slagCount;

	public BlastFurnaceRecipeSerializer(ResourceLocation key, Object input, Object output, int amount, int slagCount) {
		this(key, "", input, output, amount, slagCount);
	}

	public BlastFurnaceRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int amount, int slagCount) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.amount = amount;
		this.slagCount = slagCount;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		FluidStack stack = MiscHelper.INSTANCE.getFluidStack(output, amount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BlastFurnaceRec recipe = new BlastFurnaceRec(group, ing, stack, slagCount);
		JsonObject json = MiscHelper.INSTANCE.serialize(BlastFurnaceRec.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "crossroads:cr_blast_furnace");
		return json;
	}

	public record BlastFurnaceRec(String group, Ingredient input, FluidStack output, int slag) {
		public static final Codec<BlastFurnaceRec> CODEC = RecordCodecBuilder.create(instance->instance.group(
				CraftingUtil.recipeGroupFieldCodec().forGetter(BlastFurnaceRec::group),
				Ingredient.CODEC.fieldOf("ingredient").forGetter(BlastFurnaceRec::input),
				FluidStack.OPTIONAL_CODEC.fieldOf("output").forGetter(BlastFurnaceRec::output),
				ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("slag", 0).forGetter(BlastFurnaceRec::slag)).
				apply(instance, BlastFurnaceRec::new));
	}
}
