package thelm.jaopca.compat.techreborn.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sinytra.fabric.transfer_api.compat.NeoCompatUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import reborncore.common.fluid.FluidValue;
import reborncore.common.fluid.container.FluidInstance;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class IndustrialGrinderRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final Object fluidInput;
	public final long fluidInputAmount;
	public final int power;
	public final int time;
	public final Object[] output;

	public IndustrialGrinderRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, long fluidInputAmount, int power, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.power = power;
		this.time = time;
		this.output = Objects.requireNonNull(output);
	}

	@Override
	public JsonElement get() {
		SizedIngredient ing = MiscHelper.INSTANCE.getSizedIngredient(itemInput, itemInputCount);
		FluidStack fluidIng = MiscHelper.INSTANCE.getFluidStack(fluidInput, 1000);
		if(ing == null && fluidIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+itemInput+", "+fluidInput);
		}
		FluidInstance fluidInst = new FluidInstance(NeoCompatUtil.toFluidStorageView(fluidIng), new FluidValue(fluidInputAmount));
		List<ItemStack> outputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			ItemStack is = MiscHelper.INSTANCE.getItemStack(out, count);
			if(is == null) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(is);
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		IndustrialGrinderRecipe recipe = new IndustrialGrinderRecipe(List.of(ing), outputs, power, time, fluidInst);
		JsonObject json = MiscHelper.INSTANCE.serialize(IndustrialGrinderRecipe.CODEC, recipe).getAsJsonObject();
		json.addProperty("type", "techreborn:industrial_grinder");
		return json;
	}

	public record IndustrialGrinderRecipe(List<SizedIngredient> ingredients, List<ItemStack> outputs, int power, int time, FluidInstance fluid) {
		public static final Codec<IndustrialGrinderRecipe> CODEC = RecordCodecBuilder.create(instance->instance.group(
				SizedIngredient.FLAT_CODEC.listOf().fieldOf("ingredients").forGetter(IndustrialGrinderRecipe::ingredients),
				ItemStack.CODEC.listOf().fieldOf("outputs").forGetter(IndustrialGrinderRecipe::outputs),
				Codec.INT.fieldOf("power").forGetter(IndustrialGrinderRecipe::power),
				Codec.INT.fieldOf("time").forGetter(IndustrialGrinderRecipe::time),
				FluidInstance.CODEC.fieldOf("fluid").forGetter(IndustrialGrinderRecipe::fluid)).
				apply(instance, IndustrialGrinderRecipe::new));
	}
}
