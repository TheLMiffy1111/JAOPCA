package thelm.jaopca.compat.mekanism;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.providers.IGasProvider;
import mekanism.api.providers.ISlurryProvider;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.GasStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.SlurryStackIngredient;
import mekanism.api.recipes.ingredients.chemical.IGasIngredient;
import mekanism.api.recipes.ingredients.chemical.ISlurryIngredient;
import mekanism.api.recipes.ingredients.creator.IChemicalIngredientCreator;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import thelm.jaopca.api.ingredients.CompoundIngredientObject;
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
	private static final Logger LOGGER = LogManager.getLogger();

	private MekanismHelper() {}

	public Set<ResourceLocation> getSlurryTags() {
		return ApiImpl.INSTANCE.getTags(MekanismAPI.SLURRY_REGISTRY_NAME);
	}

	public ItemStackIngredient getItemStackIngredient(Object obj, int count) {
		SizedIngredient ing = MiscHelper.INSTANCE.getSizedIngredient(obj, count);
		return ing == null ? null : ItemStackIngredient.of(ing);
	}

	public FluidStackIngredient getFluidStackIngredient(Object obj, int amount) {
		SizedFluidIngredient ing = MiscHelper.INSTANCE.getSizedFluidIngredient(obj, amount);
		return ing == null ? null : FluidStackIngredient.of(ing);
	}

	public GasStack getGasStack(Object obj, int amount) {
		GasStack ret = getPreferredGasStack(getGasIngredientResolved(obj).getRight(), amount);
		return ret.isEmpty() ? GasStack.EMPTY : ret;
	}

	public IGasIngredient getGasIngredient(Object obj) {
		return getGasIngredientResolved(obj).getLeft();
	}

	public GasStackIngredient getGasStackIngredient(Object obj, int amount) {
		IGasIngredient ing = getGasIngredient(obj);
		return ing == null ? null : GasStackIngredient.of(ing, amount);
	}

	public Pair<IGasIngredient, Set<Gas>> getGasIngredientResolved(Object obj) {
		IGasIngredient ing = null;
		Set<Gas> gases = new HashSet<>();
		IChemicalIngredientCreator<Gas, IGasIngredient> creator = IngredientCreatorAccess.gas();
		switch(obj) {
		case Supplier<?> supplier -> {
			Pair<IGasIngredient, Set<Gas>> pair = getGasIngredientResolved(supplier.get());
			ing = pair.getLeft();
			gases.addAll(pair.getRight());
		}
		case CompoundIngredientObject(CompoundIngredientObject.Type type, Object[] ingredients) -> {
			List<Pair<IGasIngredient, Set<Gas>>> ings = Arrays.stream(ingredients).map(this::getGasIngredientResolved).toList();
			if(ings.size() == 1) {
				Pair<IGasIngredient, Set<Gas>> pair = ings.get(0);
				ing = pair.getLeft();
				gases.addAll(pair.getRight());
			}
			else if(ings.size() > 1) {
				switch(type) {
				case UNION -> {
					if(ings.stream().allMatch(p->p.getRight().isEmpty())) {
						break;
					}
					ing = creator.ofIngredients(ings.stream().filter(p->!p.getRight().isEmpty()).map(Pair::getLeft).toArray(IGasIngredient[]::new));
					gases.addAll(ings.stream().map(Pair::getRight).reduce(new HashSet<>(), (s1, s2)->{
						s1.addAll(s2);
						return s1;
					}));
				}
				case INTERSECTION -> {
					if(ings.stream().anyMatch(p->p.getRight().isEmpty())) {
						break;
					}
					ing = creator.intersection(ings.stream().map(Pair::getLeft).toArray(IGasIngredient[]::new));
					gases.addAll(ings.stream().map(Pair::getRight).reduce(Sets.newHashSet(MekanismAPI.GAS_REGISTRY), (s1, s2)->{
						s1.retainAll(s2);
						return s1;
					}));
				}
				case DIFFERENCE -> {
					Pair<IGasIngredient, Set<Gas>> firstPair = ings.get(0);
					if(firstPair.getRight().isEmpty()) {
						break;
					}
					ing = creator.difference(firstPair.getLeft(), creator.ofIngredients(ings.stream().skip(1).filter(p->!p.getRight().isEmpty()).map(Pair::getLeft).toArray(IGasIngredient[]::new)));
					gases.addAll(firstPair.getRight());
					gases.removeAll(ings.stream().skip(1).map(Pair::getRight).reduce(new HashSet<>(), (s1, s2)->{
						s1.addAll(s2);
						return s1;
					}));
				}
				}
			}
		}
		case IGasIngredient gasIng -> {
			ing = gasIng;
			// We can't know what gases the ingredient can have so assume all
			MekanismAPI.GAS_REGISTRY.forEach(gases::add);
		}
		case String str -> {
			ResourceLocation location = ResourceLocation.parse(str);
			ing = creator.tag(getGasTagKey(location));
			gases.addAll(getGasTagValues(location));
		}
		case ResourceLocation location -> {
			ing = creator.tag(getGasTagKey(location));
			gases.addAll(getGasTagValues(location));
		}
		case TagKey<?> key -> {
			ing = creator.tag(getGasTagKey(key.location()));
			gases.addAll(getGasTagValues(key.location()));
		}
		case GasStack stack -> {
			if(!stack.isEmpty()) {
				ing = creator.of(stack);
				gases.add(stack.getChemical());
			}
		}
		case GasStack[] stacks -> {
			List<GasStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.of(nonEmpty.toArray(GasStack[]::new));
				nonEmpty.stream().map(GasStack::getChemical).forEach(gases::add);
			}
		}
		case IGasProvider gas -> {
			if(!gas.getChemical().isEmptyType()) {
				ing = creator.of(gas);
				gases.add(gas.getChemical());
			}
		}
		case IGasProvider[] gasez -> {
			List<Gas> nonEmpty = Arrays.stream(gasez).map(IGasProvider::getChemical).filter(g->!g.isEmptyType()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.of(nonEmpty.stream());
				gases.addAll(nonEmpty);
			}
		}
		case JsonElement json -> {
			ing = creator.codec().parse(JsonOps.INSTANCE, json).resultOrPartial(LOGGER::warn).orElse(null);
			// We can't know what gases the ingredient can have so assume all
			MekanismAPI.GAS_REGISTRY.forEach(gases::add);
		}
		default -> {}
		}
		gases.remove(MekanismAPI.EMPTY_GAS);
		return Pair.of(gases.isEmpty() ? null : ing, gases);
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

	public SlurryStack getSlurryStack(Object obj, int amount) {
		SlurryStack ret = getPreferredSlurryStack(getSlurryIngredientResolved(obj).getRight(), amount);
		return ret.isEmpty() ? SlurryStack.EMPTY : ret;
	}

	public ISlurryIngredient getSlurryIngredient(Object obj) {
		return getSlurryIngredientResolved(obj).getLeft();
	}

	public SlurryStackIngredient getSlurryStackIngredient(Object obj, int amount) {
		ISlurryIngredient ing = getSlurryIngredient(obj);
		return ing == null ? null : SlurryStackIngredient.of(ing, amount);
	}

	public Pair<ISlurryIngredient, Set<Slurry>> getSlurryIngredientResolved(Object obj) {
		ISlurryIngredient ing = null;
		Set<Slurry> slurries = new HashSet<>();
		IChemicalIngredientCreator<Slurry, ISlurryIngredient> creator = IngredientCreatorAccess.slurry();
		switch(obj) {
		case Supplier<?> supplier -> {
			Pair<ISlurryIngredient, Set<Slurry>> pair = getSlurryIngredientResolved(supplier.get());
			ing = pair.getLeft();
			slurries.addAll(pair.getRight());
		}
		case CompoundIngredientObject(CompoundIngredientObject.Type type, Object[] ingredients) -> {
			List<Pair<ISlurryIngredient, Set<Slurry>>> ings = Arrays.stream(ingredients).map(this::getSlurryIngredientResolved).toList();
			if(ings.size() == 1) {
				Pair<ISlurryIngredient, Set<Slurry>> pair = ings.get(0);
				ing = pair.getLeft();
				slurries.addAll(pair.getRight());
			}
			else if(ings.size() > 1) {
				switch(type) {
				case UNION -> {
					if(ings.stream().allMatch(p->p.getRight().isEmpty())) {
						break;
					}
					ing = creator.ofIngredients(ings.stream().filter(p->!p.getRight().isEmpty()).map(Pair::getLeft).toArray(ISlurryIngredient[]::new));
					slurries.addAll(ings.stream().map(Pair::getRight).reduce(new HashSet<>(), (s1, s2)->{
						s1.addAll(s2);
						return s1;
					}));
				}
				case INTERSECTION -> {
					if(ings.stream().anyMatch(p->p.getRight().isEmpty())) {
						break;
					}
					ing = creator.intersection(ings.stream().map(Pair::getLeft).toArray(ISlurryIngredient[]::new));
					slurries.addAll(ings.stream().map(Pair::getRight).reduce(Sets.newHashSet(MekanismAPI.SLURRY_REGISTRY), (s1, s2)->{
						s1.retainAll(s2);
						return s1;
					}));
				}
				case DIFFERENCE -> {
					Pair<ISlurryIngredient, Set<Slurry>> firstPair = ings.get(0);
					if(firstPair.getRight().isEmpty()) {
						break;
					}
					ing = creator.difference(firstPair.getLeft(), creator.ofIngredients(ings.stream().skip(1).filter(p->!p.getRight().isEmpty()).map(Pair::getLeft).toArray(ISlurryIngredient[]::new)));
					slurries.addAll(firstPair.getRight());
					slurries.removeAll(ings.stream().skip(1).map(Pair::getRight).reduce(new HashSet<>(), (s1, s2)->{
						s1.addAll(s2);
						return s1;
					}));
				}
				}
			}
		}
		case ISlurryIngredient slurryIng -> {
			ing = slurryIng;
			// We can't know what slurries the ingredient can have so assume all
			MekanismAPI.SLURRY_REGISTRY.forEach(slurries::add);
		}
		case String str -> {
			ResourceLocation location = ResourceLocation.parse(str);
			ing = creator.tag(getSlurryTagKey(location));
			slurries.addAll(getSlurryTagValues(location));
		}
		case ResourceLocation location -> {
			ing = creator.tag(getSlurryTagKey(location));
			slurries.addAll(getSlurryTagValues(location));
		}
		case TagKey<?> key -> {
			ing = creator.tag(getSlurryTagKey(key.location()));
			slurries.addAll(getSlurryTagValues(key.location()));
		}
		case SlurryStack stack -> {
			if(stack.isEmpty()) {
				ing = creator.of(stack);
				slurries.add(stack.getChemical());
			}
		}
		case SlurryStack[] stacks -> {
			List<SlurryStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.of(nonEmpty.toArray(SlurryStack[]::new));
				nonEmpty.stream().map(SlurryStack::getChemical).forEach(slurries::add);
			}
		}
		case ISlurryProvider slurry -> {
			if(!slurry.getChemical().isEmptyType()) {
				ing = creator.of(slurry);
				slurries.add(slurry.getChemical());
			}
		}
		case ISlurryProvider[] slurriez -> {
			List<Slurry> nonEmpty = Arrays.stream(slurriez).map(ISlurryProvider::getChemical).filter(s->!s.isEmptyType()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.of(nonEmpty.stream());
				slurries.addAll(nonEmpty);
			}
		}
		case JsonElement json -> {
			ing = creator.codec().parse(JsonOps.INSTANCE, json).resultOrPartial(LOGGER::warn).orElse(null);
			// We can't know what gases the ingredient can have so assume all
			MekanismAPI.SLURRY_REGISTRY.forEach(slurries::add);
		}
		default -> {}
		}
		slurries.remove(MekanismAPI.EMPTY_SLURRY);
		return Pair.of(slurries.isEmpty() ? null : ing, slurries);
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
}
