package thelm.jaopca.compat.assemblylinemachines.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.haydenb.assemblylinemachines.block.rudimentary.BlockHandGrinder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class GrinderRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final int grinds;
	public final int tier;
	public final boolean requiresMachine;
	public final float doubleChance;

	public GrinderRecipeSerializer(ResourceLocation key, Object input, Object output, int outputCount, int grinds, int tier, boolean requiresMachine, float doubleChance) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.grinds = grinds;
		this.tier = tier;
		this.requiresMachine = requiresMachine;
		this.doubleChance = doubleChance;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			LOGGER.warn("Empty output in recipe {}: {}", key, output);
		}
		BlockHandGrinder.Blade blade;
		if(tier <= 0) {
			blade = BlockHandGrinder.Blade.TITANIUM;
		}
		else if(tier == 1) {
			blade = BlockHandGrinder.Blade.PUREGOLD;
		}
		else {
			blade = BlockHandGrinder.Blade.STEEL;
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "assemblylinemachines:grinder");
		json.add("input", ing.toJson());
		json.add("output", json);
		JsonObject resultJson = new JsonObject();
		resultJson.addProperty("item", stack.getItem().getRegistryName().toString());
		resultJson.addProperty("count", stack.getCount());
		json.add("output", resultJson);
		json.addProperty("grinds", grinds);
		json.addProperty("bladetype", blade.name());
		json.addProperty("machine_required", requiresMachine);
		json.addProperty("chanceToDouble", doubleChance);

		return json;
	}
}
