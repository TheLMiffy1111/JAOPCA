package thelm.jaopca.compat.exnihilosequentia.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import novamachina.exnihilosequentia.common.crafting.sieve.MeshWithChance;
import novamachina.exnihilosequentia.common.item.mesh.MeshType;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.MiscHelper;

public class SieveRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final Number[] meshChances;
	public final boolean waterlogged;

	public SieveRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, Number[] meshChances, boolean waterlogged) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.meshChances = meshChances;
		this.waterlogged = waterlogged;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == EmptyIngredient.INSTANCE) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		List<MeshWithChance> chances = new ArrayList<>();
		int i = 0;
		while(i+1 < meshChances.length) {
			int id = meshChances[i].intValue();
			float chance = meshChances[i+1].floatValue();
			i += 2;
			chances.add(new MeshWithChance(MeshType.values()[id], chance));
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "exnihilosequentia:sieve");
		json.add("input", ing.toJson());
		json.add("result", MiscHelper.INSTANCE.serializeItemStack(stack));
		JsonArray rollsJson = new JsonArray();
		for(MeshWithChance chance : chances) {
			rollsJson.add(chance.serialize());
		}
		json.addProperty("waterlogged", waterlogged);

		return json;
	}
}
