package thelm.jaopca.compat.crossroads;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.crossroads.recipes.BlastFurnaceRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.CrucibleRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.FluidCoolingRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.MillRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.OreCleanserRecipeSerializer;
import thelm.jaopca.compat.crossroads.recipes.StampMillRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CrossroadsHelper {

	public static final CrossroadsHelper INSTANCE = new CrossroadsHelper();
	private static final Logger LOGGER = LogManager.getLogger();

	private CrossroadsHelper() {}

	public FluidIngredient getFluidIngredient(Object obj) {
		return getFluidIngredientResolved(obj).getLeft();
	}

	public Pair<FluidIngredient, Set<Fluid>> getFluidIngredientResolved(Object obj) {
		FluidIngredient ing = null;
		Set<Fluid> fluids = new HashSet<>();
		IMiscHelper helper = MiscHelper.INSTANCE;
		switch(obj) {
		case Supplier<?> supplier -> {
			Pair<FluidIngredient, Set<Fluid>> pair = getFluidIngredientResolved(((Supplier<?>)obj).get());
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
			ing = FluidIngredient.of(helper.getFluidTagKey(location));
			fluids.addAll(helper.getFluidTagValues(location));
		}
		case ResourceLocation location -> {
			ing = FluidIngredient.of(helper.getFluidTagKey(location));
			fluids.addAll(helper.getFluidTagValues(location));
		}
		case TagKey<?> key -> {
			ing = FluidIngredient.of(key);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		case FluidStack stack -> {
			if(!stack.isEmpty()) {
				ing = FluidIngredient.of(stack);
				fluids.add(stack.getFluid());
			}
		}
		case FluidStack[] stacks -> {
			List<FluidStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = FluidIngredient.of(nonEmpty.toArray());
				nonEmpty.stream().map(FluidStack::getFluid).forEach(fluids::add);
			}
		}
		case Holder<?> holder -> {
			if(holder.isBound() && holder.value() instanceof Fluid fluid && fluid != Fluids.EMPTY) {
				ing = FluidIngredient.of(fluid);
				fluids.add(fluid);
			}
		}
		case @SuppressWarnings("rawtypes") Holder[] holders -> {
			List<Fluid> nonEmpty = Arrays.stream(holders).
					filter(Holder::isBound).map(Holder::value).
					filter(Fluid.class::isInstance).map(Fluid.class::cast).
					filter(f->f != Fluids.EMPTY).toList();
			if(!nonEmpty.isEmpty()) {
				ing = FluidIngredient.of(nonEmpty.toArray());
				fluids.addAll(nonEmpty);
			}
		}
		case Fluid fluid -> {
			if(fluid != Fluids.EMPTY) {
				ing = FluidIngredient.of(fluid);
				fluids.add(fluid);
			}
		}
		case Fluid[] fluidz -> {
			List<Fluid> nonEmpty = Arrays.stream(fluidz).filter(f->f != Fluids.EMPTY).toList();
			if(!nonEmpty.isEmpty()) {
				ing = FluidIngredient.of(nonEmpty.toArray());
				fluids.addAll(nonEmpty);
			}
		}
		case IFluidLike fluid -> {
			if(fluid.asFluid() != Fluids.EMPTY) {
				ing = FluidIngredient.of(fluid.asFluid());
				fluids.add(fluid.asFluid());
			}
		}
		case IFluidLike[] fluidz -> {
			List<Fluid> nonEmpty = Arrays.stream(fluidz).map(IFluidLike::asFluid).filter(f->f != Fluids.EMPTY).toList();
			if(!nonEmpty.isEmpty()) {
				ing = FluidIngredient.of(nonEmpty.toArray());
				fluids.addAll(nonEmpty);
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

	public boolean registerBlastFurnaceRecipe(ResourceLocation key, String group, Object input, Object output, int amount, int slagCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlastFurnaceRecipeSerializer(key, group, input, output, amount, slagCount));
	}

	public boolean registerBlastFurnaceRecipe(ResourceLocation key, Object input, Object output, int amount, int slagCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new BlastFurnaceRecipeSerializer(key, input, output, amount, slagCount));
	}

	public boolean registerCrucibleRecipe(ResourceLocation key, String group, Object input, Object output, int amount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeSerializer(key, group, input, output, amount));
	}

	public boolean registerCrucibleRecipe(ResourceLocation key, Object input, Object output, int amount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeSerializer(key, input, output, amount));
	}

	public boolean registerFluidCoolingRecipe(ResourceLocation key, String group, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FluidCoolingRecipeSerializer(key, group, input, inputAmount, output, outputCount, maxTemp, addedHeat));
	}

	public boolean registerFluidCoolingRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, float maxTemp, float addedHeat) {
		return ApiImpl.INSTANCE.registerRecipe(key, new FluidCoolingRecipeSerializer(key, input, inputAmount, output, outputCount, maxTemp, addedHeat));
	}

	public boolean registerMillRecipe(ResourceLocation key, String group, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillRecipeSerializer(key, group, input, output));
	}

	public boolean registerMillRecipe(ResourceLocation key, Object input, Object... output) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MillRecipeSerializer(key, input, output));
	}

	public boolean registerOreCleanserRecipe(ResourceLocation key, String group, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreCleanserRecipeSerializer(key, group, input, output, count));
	}

	public boolean registerOreCleanserRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreCleanserRecipeSerializer(key, input, output, count));
	}

	public boolean registerStampMillRecipe(ResourceLocation key, String group, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampMillRecipeSerializer(key, group, input, output, count));
	}

	public boolean registerStampMillRecipe(ResourceLocation key, Object input, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampMillRecipeSerializer(key, input, output, count));
	}
}
