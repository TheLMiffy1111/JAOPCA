package thelm.jaopca.compat.electrodynamics;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.electrodynamics.recipes.ChemicalCrystallizerRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.ChemicalReactorRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.ElectrolosisChamberRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.LatheRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.MineralCrusherRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.MineralGrinderRecipeSerializer;
import thelm.jaopca.compat.electrodynamics.recipes.MineralWasherRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;
import voltaic.api.gas.Gas;
import voltaic.api.gas.GasStack;
import voltaic.common.recipe.recipeutils.CountableIngredient;
import voltaic.common.recipe.recipeutils.FluidIngredient;
import voltaic.common.recipe.recipeutils.GasIngredient;
import voltaic.registers.VoltaicGases;

public class ElectrodynamicsHelper {

	public static final ElectrodynamicsHelper INSTANCE = new ElectrodynamicsHelper();
	private static final Logger LOGGER = LogManager.getLogger();

	private ElectrodynamicsHelper() {}

	public CountableIngredient getCountableIngredient(Object obj, int count) {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(obj);
		return ing == null ? null : new CountableIngredient(ing, count);
	}

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
			ing = new FluidIngredient(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		case ResourceLocation location -> {
			ing = new FluidIngredient(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		case TagKey<?> key -> {
			ing = new FluidIngredient(helper.getFluidTagKey(key.location()), amount);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		case FluidStack stack -> {
			if(!stack.isEmpty()) {
				ing = new FluidIngredient(stack);
				fluids.add(stack.getFluid());
			}
		}
		case FluidStack[] stacks -> {
			List<FluidStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = new FluidIngredient(nonEmpty);
				nonEmpty.stream().map(FluidStack::getFluid).forEach(fluids::add);
			}
		}
		case Holder<?> holder -> {
			if(holder.isBound() && holder.value() instanceof Fluid fluid && fluid != Fluids.EMPTY) {
				ing = new FluidIngredient(fluid, amount);
				fluids.add(fluid);
			}
		}
		case @SuppressWarnings("rawtypes") Holder[] holders -> {
			List<FluidStack> nonEmpty = Arrays.stream(holders).
					filter(Holder::isBound).map(Holder::value).
					filter(Fluid.class::isInstance).map(Fluid.class::cast).
					filter(f->f != Fluids.EMPTY).map(f->new FluidStack(f, amount)).toList();
			if(!nonEmpty.isEmpty()) {
				ing = new FluidIngredient(nonEmpty);
				nonEmpty.stream().map(FluidStack::getFluid).forEach(fluids::add);
			}
		}
		case Fluid fluid -> {
			if(fluid != Fluids.EMPTY) {
				ing = new FluidIngredient(fluid, amount);
				fluids.add(fluid);
			}
		}
		case Fluid[] fluidz -> {
			List<FluidStack> nonEmpty = Arrays.stream(fluidz).filter(f->f != Fluids.EMPTY).map(f->new FluidStack(f, amount)).toList();
			if(!nonEmpty.isEmpty()) {
				ing = new FluidIngredient(nonEmpty);
				nonEmpty.stream().map(FluidStack::getFluid).forEach(fluids::add);
			}
		}
		case IFluidLike fluid -> {
			if(fluid.asFluid() != Fluids.EMPTY) {
				ing = new FluidIngredient(fluid.asFluid(), amount);
				fluids.add(fluid.asFluid());
			}
		}
		case IFluidLike[] fluidz -> {
			List<FluidStack> nonEmpty = Arrays.stream(fluidz).map(IFluidLike::asFluid).filter(f->f != Fluids.EMPTY).map(f->new FluidStack(f, amount)).toList();
			if(!nonEmpty.isEmpty()) {
				ing = new FluidIngredient(nonEmpty);
				nonEmpty.stream().map(FluidStack::getFluid).forEach(fluids::add);
			}
		}
		case JsonElement json -> {
			ing = FluidIngredient.CODEC.codec().parse(JsonOps.INSTANCE, json).resultOrPartial(LOGGER::warn).orElse(null);
			// We can't know what fluids the ingredient can have so assume all
			BuiltInRegistries.FLUID.forEach(fluids::add);
		}
		default -> {}
		}
		fluids.remove(Fluids.EMPTY);
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public GasStack getGasStack(Object obj, int amount, int temperature, int pressure) {
		GasStack ret = getPreferredGasStack(getGasIngredientResolved(obj, amount, temperature, pressure).getRight(), amount, temperature, pressure);
		return ret.isEmpty() ? GasStack.EMPTY : ret;
	}

	public GasIngredient getGasIngredient(Object obj, int amount, int temperature, int pressure) {
		return getGasIngredientResolved(obj, amount, temperature, pressure).getLeft();
	}

	public Pair<GasIngredient, Set<Gas>> getGasIngredientResolved(Object obj, int amount, int temperature, int pressure) {
		GasIngredient ing = null;
		Set<Gas> gases = new HashSet<>();
		switch(obj) {
		case Supplier<?> supplier -> {
			Pair<GasIngredient, Set<Gas>> pair = getGasIngredientResolved(supplier.get(), amount, temperature, pressure);
			ing = pair.getLeft();
			gases.addAll(pair.getRight());
		}
		case GasIngredient gasIng -> {
			ing = gasIng;
			// We can't know what fluids the ingredient can have so assume all
			VoltaicGases.GAS_REGISTRY.forEach(gases::add);
		}
		case String str -> {
			ResourceLocation location = ResourceLocation.parse(str);
			ing = new GasIngredient(getGasTagKey(location), amount, temperature, pressure);
			gases.addAll(getGasTagValues(location));
		}
		case ResourceLocation location -> {
			ing = new GasIngredient(getGasTagKey(location), amount, temperature, pressure);
			gases.addAll(getGasTagValues(location));
		}
		case TagKey<?> key -> {
			ing = new GasIngredient(getGasTagKey(key.location()), amount, temperature, pressure);
			gases.addAll(getGasTagValues(key.location()));
		}
		case GasStack stack -> {
			if(!stack.isEmpty()) {
				ing = new GasIngredient(stack);
				gases.add(stack.getGas());
			}
		}
		case GasStack[] stacks -> {
			List<GasStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = new GasIngredient(nonEmpty);
				nonEmpty.stream().map(GasStack::getGas).forEach(gases::add);
			}
		}
		case Holder<?> holder -> {
			if(holder.isBound() && holder.value() instanceof Gas gas && !gas.isEmpty()) {
				ing = new GasIngredient(gas, amount, temperature, pressure);
				gases.add(gas);
			}
		}
		case @SuppressWarnings("rawtypes") Holder[] holders -> {
			List<GasStack> nonEmpty = Arrays.stream(holders).
					filter(Holder::isBound).map(Holder::value).
					filter(Gas.class::isInstance).map(Gas.class::cast).
					filter(g->!g.isEmpty()).map(g->new GasStack(g, amount, temperature, pressure)).toList();
			if(!nonEmpty.isEmpty()) {
				ing = new GasIngredient(nonEmpty);
				nonEmpty.stream().map(GasStack::getGas).forEach(gases::add);
			}
		}
		case Gas gas -> {
			if(!gas.isEmpty()) {
				ing = new GasIngredient(gas, amount, temperature, pressure);
				gases.add(gas);
			}
		}
		case Gas[] gasez -> {
			List<GasStack> nonEmpty = Arrays.stream(gasez).filter(g->!g.isEmpty()).map(g->new GasStack(g, amount, temperature, pressure)).toList();
			if(!nonEmpty.isEmpty()) {
				ing = new GasIngredient(nonEmpty);
				nonEmpty.stream().map(GasStack::getGas).forEach(gases::add);
			}
		}
		case JsonElement json -> {
			ing = GasIngredient.CODEC.codec().parse(JsonOps.INSTANCE, json).resultOrPartial(LOGGER::warn).orElse(null);
			// We can't know what fluids the ingredient can have so assume all
			VoltaicGases.GAS_REGISTRY.forEach(gases::add);
		}
		default -> {}
		}
		gases.remove(VoltaicGases.EMPTY.get());
		return Pair.of(gases.isEmpty() ? null : ing, gases);
	}

	public TagKey<Gas> getGasTagKey(ResourceLocation location) {
		return TagKey.create(VoltaicGases.GAS_REGISTRY_KEY, location);
	}

	public Collection<Gas> getGasTagValues(ResourceLocation location) {
		return MiscHelper.INSTANCE.getTagValues(VoltaicGases.GAS_REGISTRY_KEY, location);
	}

	public GasStack getPreferredGasStack(Iterable<Gas> collection, int amount, int temperature, int pressure) {
		return new GasStack(MiscHelper.INSTANCE.getPreferredEntry(VoltaicGases.GAS_REGISTRY::getKey, collection).orElse(VoltaicGases.EMPTY.get()), amount, temperature, pressure);
	}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSerializer(key, group, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSerializer(key, group, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerMineralCrusherRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralCrusherRecipeSerializer(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSerializer(key, group, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSerializer(key, group, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerMineralGrinderRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralGrinderRecipeSerializer(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerLatheRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSerializer(key, group, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerLatheRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, double secondChance, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, experience, time, energy));
	}

	public boolean registerLatheRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSerializer(key, group, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerLatheRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LatheRecipeSerializer(key, input, inputCount, output, outputCount, experience, time, energy));
	}

	public boolean registerChemicalCrystallizerRecipe(ResourceLocation key, String group, Object input, int inputAmount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalCrystallizerRecipeSerializer(key, group, input, inputAmount, output, outputCount, experience, time, energy));
	}

	public boolean registerChemicalCrystallizerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalCrystallizerRecipeSerializer(key, input, inputAmount, output, outputCount, experience, time, energy));
	}

	public boolean registerChemicalReactorRecipe(ResourceLocation key, String group, Object[] itemInput, Object[] fluidInput, Object[] gasInput, Object[] itemOutput, Object[] fluidOutput, Object[] gasOutput, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalReactorRecipeSerializer(key, group, itemInput, fluidInput, gasInput, itemOutput, fluidOutput, gasOutput, experience, time, energy));
	}

	public boolean registerChemicalReactorRecipe(ResourceLocation key, Object[] itemInput, Object[] fluidInput, Object[] gasInput, Object[] itemOutput, Object[] fluidOutput, Object[] gasOutput, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ChemicalReactorRecipeSerializer(key, itemInput, fluidInput, gasInput, itemOutput, fluidOutput, gasOutput, experience, time, energy));
	}

	public boolean registerMineralWasherRecipe(ResourceLocation key, String group, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralWasherRecipeSerializer(key, group, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputAmount, experience, time, energy));
	}

	public boolean registerMineralWasherRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MineralWasherRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputAmount, experience, time, energy));
	}

	public boolean registerElectrolosisChamberRecipe(ResourceLocation key, String group, Object input, int inputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ElectrolosisChamberRecipeSerializer(key, group, input, inputAmount, output, outputAmount, experience, time, energy));
	}

	public boolean registerElectrolosisChamberRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputAmount, double experience, int time, double energy) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ElectrolosisChamberRecipeSerializer(key, input, inputAmount, output, outputAmount, experience, time, energy));
	}
}
