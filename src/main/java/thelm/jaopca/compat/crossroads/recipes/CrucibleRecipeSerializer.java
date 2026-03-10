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
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CrucibleRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final String group;
	public final Object input;
	public final Object output;
	public final int amount;

	public CrucibleRecipeSerializer(ResourceLocation key, Object input, Object output, int amount) {
		this(key, "", input, output, amount);
	}

	public CrucibleRecipeSerializer(ResourceLocation key, String group, Object input, Object output, int amount) {
		this.key = Objects.requireNonNull(key);
		this.group = Strings.nullToEmpty(group);
		this.input = input;
		this.output = output;
		this.amount = amount;
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
		CrucibleRec recipe = new CrucibleRec(group, ing, stack);
		JsonObject json = MiscHelper.INSTANCE.serialize(CrucibleRec.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "crossroads:crucible");
		return json;
	}

	public record CrucibleRec(String group, Ingredient input, FluidStack output) {
		public static final Codec<CrucibleRec> CODEC = RecordCodecBuilder.create(instance->instance.group(
				CraftingUtil.recipeGroupFieldCodec().forGetter(CrucibleRec::group),
				Ingredient.CODEC.fieldOf("input").forGetter(CrucibleRec::input),
				FluidStack.OPTIONAL_CODEC.fieldOf("output").forGetter(CrucibleRec::output)).
				apply(instance, CrucibleRec::new));
	}
}
