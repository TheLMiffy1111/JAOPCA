package thelm.jaopca.compat.create;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.create.recipes.CrushingRecipeSerializer;
import thelm.jaopca.compat.create.recipes.MillingRecipeSerializer;
import thelm.jaopca.compat.create.recipes.PressingRecipeSerializer;
import thelm.jaopca.compat.create.recipes.SplashingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CreateHelper {

	public static final CreateHelper INSTANCE = new CreateHelper();
	private static final Logger LOGGER = LogManager.getLogger();

	private CreateHelper() {}

	public FluidIngredient getFluidIngredient(Object obj, int amount) {
		return getFluidIngredientResolved(obj, amount).getLeft();
	}

	public Pair<FluidIngredient, Set<Fluid>> getFluidIngredientResolved(Object obj, int amount) {
		FluidIngredient ing = null;
		Set<Fluid> fluids = new HashSet<>();
		IMiscHelper helper = MiscHelper.INSTANCE;
		switch(obj) {
		case Supplier<?> supplier -> {
			Pair<FluidIngredient, Set<Fluid>> pair = getFluidIngredientResolved(supplier.get(), amount);
			ing = pair.getLeft();
			fluids.addAll(pair.getRight());
		}
		case FluidIngredient fluidIng -> {
			ing = fluidIng;
			// We can't know what fluids the ingredient can have so assume all
			BuiltInRegistries.FLUID.forEach(fluids::add);
		}
		case String str -> {
			ResourceLocation location = ResourceLocation.parse(str);
			ing = FluidIngredient.fromTag(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		case ResourceLocation location -> {
			ing = FluidIngredient.fromTag(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		case TagKey<?> key -> {
			ing = FluidIngredient.fromTag(helper.getFluidTagKey(key.location()), amount);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		case FluidStack stack -> {
			if(!stack.isEmpty()) {
				ing = FluidIngredient.fromFluidStack(stack);
				fluids.add(stack.getFluid());
			}
		}
		case Fluid fluid -> {
			if(fluid != Fluids.EMPTY) {
				ing = FluidIngredient.fromFluid(fluid, amount);
				fluids.add(fluid);
			}
		}
		case IFluidLike fluid -> {
			if(fluid.asFluid() != Fluids.EMPTY) {
				ing = FluidIngredient.fromFluid(fluid.asFluid(), amount);
				fluids.add(fluid.asFluid());
			}
		}
		case JsonElement json -> {
			ing = FluidIngredient.CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(LOGGER::warn).orElse(null);
			// We can't know what fluids the ingredient can have so assume all
			BuiltInRegistries.FLUID.forEach(fluids::add);
		}
		default -> {}
		}
		fluids.remove(Fluids.EMPTY);
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, time, output));
	}

	public boolean registerMillingRecipe(ResourceLocation key, Object input, int time, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillingRecipeSerializer(key, input, time, output));
	}

	public boolean registerPressingRecipe(ResourceLocation key, Object input, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressingRecipeSerializer(key, input, output, outputCount));
	}

	public boolean registerSplashingRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SplashingRecipeSerializer(key, input, output));
	}
}
