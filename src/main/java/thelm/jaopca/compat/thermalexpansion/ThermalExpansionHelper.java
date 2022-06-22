package thelm.jaopca.compat.thermalexpansion;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;

import cofh.lib.fluid.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.thermalexpansion.recipes.CentrifugeRecipeSerializer;
import thelm.jaopca.compat.thermalexpansion.recipes.ChillerRecipeSerializer;
import thelm.jaopca.compat.thermalexpansion.recipes.PressRecipeSerializer;
import thelm.jaopca.compat.thermalexpansion.recipes.PulverizerRecipeSerializer;
import thelm.jaopca.compat.thermalexpansion.recipes.SmelterRecipeSerializer;
import thelm.jaopca.compat.thermalexpansion.recipes.SmelterRecycleRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class ThermalExpansionHelper {

	public static final ThermalExpansionHelper INSTANCE = new ThermalExpansionHelper();

	private ThermalExpansionHelper() {}

	public FluidIngredient getFluidIngredient(Object obj, int amount) {
		return getFluidIngredientResolved(obj, amount).getLeft();
	}

	public Pair<FluidIngredient, Set<Fluid>> getFluidIngredientResolved(Object obj, int amount) {
		FluidIngredient ing = null;
		Set<Fluid> fluids = new HashSet<>();
		IMiscHelper helper = MiscHelper.INSTANCE;
		if(obj instanceof Supplier<?>) {
			Pair<FluidIngredient, Set<Fluid>> pair = getFluidIngredientResolved(((Supplier<?>)obj).get(), amount);
			ing = pair.getLeft();
			fluids.addAll(pair.getRight());
		}
		else if(obj instanceof FluidIngredient) {
			ing = (FluidIngredient)obj;
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
		else if(obj instanceof String) {
			ResourceLocation location = new ResourceLocation((String)obj);
			ing = FluidIngredient.of(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = FluidIngredient.of(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = FluidIngredient.of(key, amount);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		else if(obj instanceof FluidStack stack) {
			ing = FluidIngredient.of(stack);
			fluids.add(stack.getFluid());
		}
		else if(obj instanceof FluidStack[] stacks) {
			ing = FluidIngredient.of(stacks);
			Arrays.stream(stacks).map(FluidStack::getFluid).forEach(fluids::add);
		}
		else if(obj instanceof Fluid fluid) {
			ing = FluidIngredient.of(new FluidStack(fluid, amount));
			fluids.add(fluid);
		}
		else if(obj instanceof Fluid[] fluidz) {
			ing = FluidIngredient.of(Arrays.stream(fluidz).map(f->new FluidStack(f, amount)).toArray(FluidStack[]::new));
			Collections.addAll(fluids, fluidz);
		}
		else if(obj instanceof JsonElement) {
			ing = FluidIngredient.fromJson((JsonElement)obj);
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public boolean registerPulverizerRecipe(ResourceLocation key, Object input, int inputCount, Object[] output, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PulverizerRecipeSerializer(key, input, inputCount, output, energy, experience));
	}

	public boolean registerSmelterRecipe(ResourceLocation key, Object[] input, Object[] output, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SmelterRecipeSerializer(key, input, output, energy, experience));
	}

	public boolean registerSmelterRecycleRecipe(ResourceLocation key, Object[] input, Object[] output, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SmelterRecycleRecipeSerializer(key, input, output, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, float itemOutputChance, Object fluidOutput, int fluidOutputAmount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSerializer(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, itemOutputChance, fluidOutput, fluidOutputAmount, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSerializer(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, fluidOutput, fluidOutputAmount, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, float itemOutputChance, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSerializer(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, itemOutputChance, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSerializer(key, input, inputCount, secondInput, secondInputCount, itemOutput, itemOutputCount, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object itemOutput, int itemOutputCount, float itemOutputChance, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSerializer(key, input, inputCount, itemOutput, itemOutputCount, itemOutputChance, energy, experience));
	}

	public boolean registerPressRecipe(ResourceLocation key, Object input, int inputCount, Object itemOutput, int itemOutputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressRecipeSerializer(key, input, inputCount, itemOutput, itemOutputCount, energy, experience));
	}

	public boolean registerChillerRecipe(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object itemInput, int itemInputCount, Object output, int outputCount, float outputChance, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChillerRecipeSerializer(key, fluidInput, fluidInputAmount, itemInput, itemInputCount, output, outputCount, outputChance, energy, experience));
	}

	public boolean registerChillerRecipe(ResourceLocation key, Object fluidInput, int fluidInputAmount, Object itemInput, int itemInputCount, Object output, int outputCount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChillerRecipeSerializer(key, fluidInput, fluidInputAmount, itemInput, itemInputCount, output, outputCount, energy, experience));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, int inputCount, Object[] output, Object fluidOutput, int fluidOutputAmount, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, inputCount, output, fluidOutput, fluidOutputAmount, energy, experience));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, int inputCount, Object[] output, int energy, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, inputCount, output, energy, experience));
	}
}
