package thelm.jaopca.compat.mekanism;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.providers.IGasProvider;
import mekanism.api.providers.ISlurryProvider;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.GasStackIngredient;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.SlurryStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IChemicalStackIngredientCreator;
import mekanism.api.recipes.ingredients.creator.IFluidStackIngredientCreator;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.mekanism.recipes.CombiningRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.CrushingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.CrystallizingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.DissolutionRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.EnrichingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.InjectingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.PurifyingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.WashingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class MekanismHelper {

	public static final MekanismHelper INSTANCE = new MekanismHelper();

	private MekanismHelper() {}

	public Set<ResourceLocation> getSlurryTags() {
		return ApiImpl.INSTANCE.getTags(MekanismAPI.SLURRY_REGISTRY_NAME);
	}

	public ItemStackIngredient getItemStackIngredient(Object obj, int count) {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(obj);
		return ing == null ? null : IngredientCreatorAccess.item().from(ing, count);
	}

	public FluidStackIngredient getFluidStackIngredient(Object obj, int amount) {
		return getFluidStackIngredientResolved(obj, amount).getLeft();
	}

	public Pair<FluidStackIngredient, Set<Fluid>> getFluidStackIngredientResolved(Object obj, int amount) {
		FluidStackIngredient ing = null;
		Set<Fluid> fluids = new HashSet<>();
		IMiscHelper helper = MiscHelper.INSTANCE;
		IFluidStackIngredientCreator creator = IngredientCreatorAccess.fluid();
		if(obj instanceof Supplier<?>) {
			Pair<FluidStackIngredient, Set<Fluid>> pair = getFluidStackIngredientResolved(((Supplier<?>)obj).get(), amount);
			ing = pair.getLeft();
			fluids.addAll(pair.getRight());
		}
		else if(obj instanceof FluidStackIngredient) {
			ing = (FluidStackIngredient)obj;
			// We can't know what fluids the ingredient can have so assume all
			BuiltInRegistries.FLUID.forEach(fluids::add);
		}
		else if(obj instanceof String) {
			ResourceLocation location = new ResourceLocation((String)obj);
			ing = creator.from(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = creator.from(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = creator.from(key, amount);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		else if(obj instanceof FluidStack stack) {
			if(!stack.isEmpty()) {
				ing = creator.from(stack);
				fluids.add(stack.getFluid());
			}
		}
		else if(obj instanceof FluidStack[] stacks) {
			List<FluidStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.from(nonEmpty.stream().map(creator::from));
				nonEmpty.stream().map(FluidStack::getFluid).forEach(fluids::add);
			}
		}
		else if(obj instanceof Fluid fluid) {
			if(fluid != Fluids.EMPTY) {
				ing = creator.from(fluid, amount);
				fluids.add(fluid);
			}
		}
		else if(obj instanceof Fluid[] fluidz) {
			List<Fluid> nonEmpty = Arrays.stream(fluidz).filter(f->f != Fluids.EMPTY).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.from(nonEmpty.stream().map(f->creator.from(f, amount)));
				fluids.addAll(nonEmpty);
			}
		}
		else if(obj instanceof IFluidLike fluid) {
			if(fluid.asFluid() != Fluids.EMPTY) {
				ing = creator.from(fluid.asFluid(), amount);
				fluids.add(fluid.asFluid());
			}
		}
		else if(obj instanceof IFluidLike[] fluidz) {
			List<Fluid> nonEmpty = Arrays.stream(fluidz).map(IFluidLike::asFluid).filter(f->f != Fluids.EMPTY).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.from(nonEmpty.stream().map(f->creator.from(f, amount)));
				fluids.addAll(nonEmpty);
			}
		}
		else if(obj instanceof JsonElement) {
			ing = creator.codec().parse(JsonOps.INSTANCE, (JsonElement)obj).result().get();
			// We can't know what fluids the ingredient can have so assume all
			BuiltInRegistries.FLUID.forEach(fluids::add);
		}
		fluids.remove(Fluids.EMPTY);
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public GasStackIngredient getGasStackIngredient(Object obj, int amount) {
		return getGasStackIngredientResolved(obj, amount).getLeft();
	}

	public Pair<GasStackIngredient, Set<Gas>> getGasStackIngredientResolved(Object obj, int amount) {
		GasStackIngredient ing = null;
		Set<Gas> gases = new HashSet<>();
		IChemicalStackIngredientCreator<Gas, GasStack, GasStackIngredient> creator = IngredientCreatorAccess.gas();
		if(obj instanceof Supplier<?>) {
			Pair<GasStackIngredient, Set<Gas>> pair = getGasStackIngredientResolved(((Supplier<?>)obj).get(), amount);
			ing = pair.getLeft();
			gases.addAll(pair.getRight());
		}
		else if(obj instanceof GasStackIngredient) {
			ing = (GasStackIngredient)obj;
			// We can't know what gases the ingredient can have so assume all
			MekanismAPI.GAS_REGISTRY.forEach(gases::add);
		}
		else if(obj instanceof String) {
			ResourceLocation location = new ResourceLocation((String)obj);
			ing = creator.from(getGasTagKey(location), amount);
			gases.addAll(getGasTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = creator.from(getGasTagKey(location), amount);
			gases.addAll(getGasTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = creator.from(key, amount);
			gases.addAll(getGasTagValues(key.location()));
		}
		else if(obj instanceof GasStack stack) {
			if(!stack.isEmpty()) {
				ing = creator.from(stack);
				gases.add(stack.getType());
			}
		}
		else if(obj instanceof GasStack[] stacks) {
			List<GasStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.from(nonEmpty.stream().map(creator::from));
				nonEmpty.stream().map(GasStack::getType).forEach(gases::add);
			}
		}
		else if(obj instanceof IGasProvider gas) {
			if(!gas.getChemical().isEmptyType()) {
				ing = creator.from(gas, amount);
				gases.add(gas.getChemical());
			}
		}
		else if(obj instanceof IGasProvider[] gasez) {
			List<Gas> nonEmpty = Arrays.stream(gasez).map(IGasProvider::getChemical).filter(g->!g.isEmptyType()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.from(nonEmpty.stream().map(g->creator.from(g, amount)));
				gases.addAll(nonEmpty);
			}
		}
		else if(obj instanceof JsonElement) {
			ing = creator.codec().parse(JsonOps.INSTANCE, (JsonElement)obj).result().get();
			// We can't know what gases the ingredient can have so assume all
			MekanismAPI.GAS_REGISTRY.forEach(gases::add);
		}
		gases.remove(MekanismAPI.EMPTY_GAS);
		return Pair.of(gases.isEmpty() ? null : ing, gases);
	}

	public SlurryStackIngredient getSlurryStackIngredient(Object obj, int amount) {
		return getSlurryStackIngredientResolved(obj, amount).getLeft();
	}

	public Pair<SlurryStackIngredient, Set<Slurry>> getSlurryStackIngredientResolved(Object obj, int amount) {
		SlurryStackIngredient ing = null;
		Set<Slurry> slurries = new HashSet<>();
		IChemicalStackIngredientCreator<Slurry, SlurryStack, SlurryStackIngredient> creator = IngredientCreatorAccess.slurry();
		if(obj instanceof Supplier<?>) {
			Pair<SlurryStackIngredient, Set<Slurry>> pair = getSlurryStackIngredientResolved(((Supplier<?>)obj).get(), amount);
			ing = pair.getLeft();
			slurries.addAll(pair.getRight());
		}
		else if(obj instanceof SlurryStackIngredient) {
			ing = (SlurryStackIngredient)obj;
			// We can't know what slurries the ingredient can have so assume all
			MekanismAPI.SLURRY_REGISTRY.forEach(slurries::add);
		}
		else if(obj instanceof String) {
			ResourceLocation location = new ResourceLocation((String)obj);
			ing = creator.from(getSlurryTagKey(location), amount);
			slurries.addAll(getSlurryTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = creator.from(getSlurryTagKey(location), amount);
			slurries.addAll(getSlurryTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = creator.from(key, amount);
			slurries.addAll(getSlurryTagValues(key.location()));
		}
		else if(obj instanceof SlurryStack stack) {
			if(!stack.isEmpty()) {
				ing = creator.from(stack);
				slurries.add(stack.getType());
			}
		}
		else if(obj instanceof SlurryStack[] stacks) {
			List<SlurryStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.from(nonEmpty.stream().map(creator::from));
				nonEmpty.stream().map(SlurryStack::getType).forEach(slurries::add);
			}
		}
		else if(obj instanceof ISlurryProvider slurry) {
			if(!slurry.getChemical().isEmptyType()) {
				ing = creator.from(slurry, amount);
				slurries.add(slurry.getChemical());
			}
		}
		else if(obj instanceof ISlurryProvider[] slurriez) {
			List<Slurry> nonEmpty = Arrays.stream(slurriez).map(ISlurryProvider::getChemical).filter(s->!s.isEmptyType()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.from(nonEmpty.stream().map(s->creator.from(s, amount)));
				slurries.addAll(nonEmpty);
			}
		}
		else if(obj instanceof JsonElement) {
			ing = creator.codec().parse(JsonOps.INSTANCE, (JsonElement)obj).result().get();
			// We can't know what slurries the ingredient can have so assume all
			MekanismAPI.SLURRY_REGISTRY.forEach(slurries::add);
		}
		slurries.remove(MekanismAPI.EMPTY_SLURRY);
		return Pair.of(slurries.isEmpty() ? null : ing, slurries);
	}

	public GasStack getGasStack(Object obj, int amount) {
		GasStack ret = getPreferredGasStack(getGasStackIngredientResolved(obj, amount).getRight(), amount);
		return ret.isEmpty() ? GasStack.EMPTY : ret;
	}

	public SlurryStack getSlurryStack(Object obj, int amount) {
		SlurryStack ret = getPreferredSlurryStack(getSlurryStackIngredientResolved(obj, amount).getRight(), amount);
		return ret.isEmpty() ? SlurryStack.EMPTY : ret;
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount));
	}

	public boolean registerEnrichingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new EnrichingRecipeSerializer(key, input, inputCount, output, outputCount));
	}

	public boolean registerCombiningRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CombiningRecipeSerializer(key, input, inputCount, secondInput, secondInputCount, output, outputCount));
	}

	public boolean registerWashingRecipe(ResourceLocation key, Object fluidInput, int fluidInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSerializer(key, fluidInput, fluidInputCount, gasInput, gasInputCount, output, outputCount));
	}

	public boolean registerCrystallizingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizingRecipeSerializer(key, input, inputCount, output, outputCount));
	}

	public boolean registerDissolutionRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new DissolutionRecipeSerializer(key, itemInput, itemInputCount, gasInput, gasInputCount, output, outputCount));
	}

	public boolean registerPurifyingRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PurifyingRecipeSerializer(key, itemInput, itemInputCount, gasInput, gasInputCount, output, outputCount));
	}

	public boolean registerInjectingRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new InjectingRecipeSerializer(key, itemInput, itemInputCount, gasInput, gasInputCount, output, outputCount));
	}

	public TagKey<Gas> getGasTagKey(ResourceLocation location) {
		return TagKey.create(MekanismAPI.GAS_REGISTRY_NAME, location);
	}

	public Collection<Gas> getGasTagValues(ResourceLocation location) {
		return MiscHelper.INSTANCE.getTagValues(MekanismAPI.GAS_REGISTRY_NAME, location);
	}

	public GasStack getPreferredGasStack(Iterable<Gas> collection, int amount) {
		return new GasStack(MiscHelper.INSTANCE.getPreferredEntry(MekanismAPI.GAS_REGISTRY::getKey, collection).orElse(MekanismAPI.EMPTY_GAS), amount);
	}

	public TagKey<Slurry> getSlurryTagKey(ResourceLocation location) {
		return TagKey.create(MekanismAPI.SLURRY_REGISTRY_NAME, location);
	}

	public Collection<Slurry> getSlurryTagValues(ResourceLocation location) {
		return MiscHelper.INSTANCE.getTagValues(MekanismAPI.SLURRY_REGISTRY_NAME, location);
	}

	public SlurryStack getPreferredSlurryStack(Iterable<Slurry> collection, int amount) {
		return new SlurryStack(MiscHelper.INSTANCE.getPreferredEntry(MekanismAPI.SLURRY_REGISTRY::getKey, collection).orElse(MekanismAPI.EMPTY_SLURRY), amount);
	}
}
