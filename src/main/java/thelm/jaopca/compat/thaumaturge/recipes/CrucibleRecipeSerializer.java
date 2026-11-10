package thelm.jaopca.compat.thaumaturge.recipes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.leclowndu93150.thaumaturge.api.recipe.ResearchGate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CrucibleRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final List<AspectInstance> aspects;
	public final Object output;
	public final int outputCount;
	public final ResearchGate research;

	public CrucibleRecipeSerializer(Identifier key, Object input, List<AspectInstance> aspects, Object output, int outputCount, ResearchGate research) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.aspects = aspects;
		this.output = output;
		this.outputCount = outputCount;
		this.research = research;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(output, outputCount);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CrucibleRecipe recipe = new CrucibleRecipe(ing, aspects, stack, Optional.ofNullable(research));
		JsonObject json = MiscHelper.INSTANCE.serialize(CrucibleRecipe.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "thaumaturge:crucible");
		return json;
	}

	public record CrucibleRecipe(Ingredient ingredient, List<AspectInstance> aspects, ItemStackTemplate result, Optional<ResearchGate> research) {
		public static final Codec<CrucibleRecipe> CODEC = RecordCodecBuilder.create(instance->instance.group(
				Ingredient.CODEC.fieldOf("catalyst").forGetter(CrucibleRecipe::ingredient),
				AspectInstance.CODEC.listOf().fieldOf("aspects").forGetter(CrucibleRecipe::aspects),
				ItemStackTemplate.CODEC.fieldOf("result").forGetter(CrucibleRecipe::result),
				ResearchGate.CODEC.optionalFieldOf("research").forGetter(CrucibleRecipe::research)).
				apply(instance, CrucibleRecipe::new));
	}
}
