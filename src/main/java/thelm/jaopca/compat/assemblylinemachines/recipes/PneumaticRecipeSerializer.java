package thelm.jaopca.compat.assemblylinemachines.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.haydenb.assemblylinemachines.registry.utils.CountIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.assemblylinemachines.AssemblyLineMachinesHelper;
import thelm.jaopca.utils.MiscHelper;

public class PneumaticRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object mold;
	public final Object output;
	public final int outputCount;
	public final int time;

	public PneumaticRecipeSerializer(ResourceLocation key, Object input, int inputCount, Object mold, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.mold = mold;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public JsonElement get() {
		CountIngredient ing = AssemblyLineMachinesHelper.INSTANCE.getCountIngredient(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Item moldItem = MiscHelper.INSTANCE.getItemStack(mold, 1).getItem();
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}

		JsonObject json = new JsonObject();
		json.addProperty("type", "assemblylinemachines:pneumatic");
		json.add("input", ing.toJson());
		json.addProperty("moldItem", ForgeRegistries.ITEMS.getKey(moldItem).toString());
		json.add("output", MiscHelper.INSTANCE.serializeItemStack(stack));
		json.addProperty("time", time);

		return json;
	}
}
