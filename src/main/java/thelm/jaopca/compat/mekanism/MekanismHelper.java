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
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.providers.IChemicalProvider;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.chemical.ChemicalIngredient;
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

	public Set<ResourceLocation> getChemicalTags() {
		return ApiImpl.INSTANCE.getTags(MekanismAPI.CHEMICAL_REGISTRY_NAME);
	}

	public ItemStackIngredient getItemStackIngredient(Object obj, int count) {
		SizedIngredient ing = MiscHelper.INSTANCE.getSizedIngredient(obj, count);
		return ing == null ? null : ItemStackIngredient.of(ing);
	}

	public FluidStackIngredient getFluidStackIngredient(Object obj, int amount) {
		SizedFluidIngredient ing = MiscHelper.INSTANCE.getSizedFluidIngredient(obj, amount);
		return ing == null ? null : FluidStackIngredient.of(ing);
	}

	public ChemicalStack getChemicalStack(Object obj, int amount) {
		ChemicalStack ret = getPreferredChemicalStack(getChemicalIngredientResolved(obj).getRight(), amount);
		return ret.isEmpty() ? ChemicalStack.EMPTY : ret;
	}

	public ChemicalIngredient getChemicalIngredient(Object obj) {
		return getChemicalIngredientResolved(obj).getLeft();
	}

	public ChemicalStackIngredient getChemicalStackIngredient(Object obj, int amount) {
		ChemicalIngredient ing = getChemicalIngredient(obj);
		return ing == null ? null : ChemicalStackIngredient.of(ing, amount);
	}

	public Pair<ChemicalIngredient, Set<Chemical>> getChemicalIngredientResolved(Object obj) {
		ChemicalIngredient ing = null;
		Set<Chemical> slurries = new HashSet<>();
		IChemicalIngredientCreator creator = IngredientCreatorAccess.chemical();
		switch(obj) {
		case Supplier<?> supplier -> {
			Pair<ChemicalIngredient, Set<Chemical>> pair = getChemicalIngredientResolved(supplier.get());
			ing = pair.getLeft();
			slurries.addAll(pair.getRight());
		}
		case CompoundIngredientObject(CompoundIngredientObject.Type type, Object[] ingredients) -> {
			List<Pair<ChemicalIngredient, Set<Chemical>>> ings = Arrays.stream(ingredients).map(this::getChemicalIngredientResolved).toList();
			if(ings.size() == 1) {
				Pair<ChemicalIngredient, Set<Chemical>> pair = ings.get(0);
				ing = pair.getLeft();
				slurries.addAll(pair.getRight());
			}
			else if(ings.size() > 1) {
				switch(type) {
				case UNION -> {
					if(ings.stream().allMatch(p->p.getRight().isEmpty())) {
						break;
					}
					ing = creator.ofIngredients(ings.stream().filter(p->!p.getRight().isEmpty()).map(Pair::getLeft).toArray(ChemicalIngredient[]::new));
					slurries.addAll(ings.stream().map(Pair::getRight).reduce(new HashSet<>(), (s1, s2)->{
						s1.addAll(s2);
						return s1;
					}));
				}
				case INTERSECTION -> {
					if(ings.stream().anyMatch(p->p.getRight().isEmpty())) {
						break;
					}
					ing = creator.intersection(ings.stream().map(Pair::getLeft).toArray(ChemicalIngredient[]::new));
					slurries.addAll(ings.stream().map(Pair::getRight).reduce(Sets.newHashSet(MekanismAPI.CHEMICAL_REGISTRY), (s1, s2)->{
						s1.retainAll(s2);
						return s1;
					}));
				}
				case DIFFERENCE -> {
					Pair<ChemicalIngredient, Set<Chemical>> firstPair = ings.get(0);
					if(firstPair.getRight().isEmpty()) {
						break;
					}
					ing = creator.difference(firstPair.getLeft(), creator.ofIngredients(ings.stream().skip(1).filter(p->!p.getRight().isEmpty()).map(Pair::getLeft).toArray(ChemicalIngredient[]::new)));
					slurries.addAll(firstPair.getRight());
					slurries.removeAll(ings.stream().skip(1).map(Pair::getRight).reduce(new HashSet<>(), (s1, s2)->{
						s1.addAll(s2);
						return s1;
					}));
				}
				}
			}
		}
		case ChemicalIngredient chemicalIng -> {
			ing = chemicalIng;
			// We can't know what slurries the ingredient can have so assume all
			MekanismAPI.CHEMICAL_REGISTRY.forEach(slurries::add);
		}
		case String str -> {
			ResourceLocation location = ResourceLocation.parse(str);
			ing = creator.tag(getChemicalTagKey(location));
			slurries.addAll(getChemicalTagValues(location));
		}
		case ResourceLocation location -> {
			ing = creator.tag(getChemicalTagKey(location));
			slurries.addAll(getChemicalTagValues(location));
		}
		case TagKey<?> key -> {
			ing = creator.tag(getChemicalTagKey(key.location()));
			slurries.addAll(getChemicalTagValues(key.location()));
		}
		case ChemicalStack stack -> {
			if(stack.isEmpty()) {
				ing = creator.of(stack);
				slurries.add(stack.getChemical());
			}
		}
		case ChemicalStack[] stacks -> {
			List<ChemicalStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.of(nonEmpty.toArray(ChemicalStack[]::new));
				nonEmpty.stream().map(ChemicalStack::getChemical).forEach(slurries::add);
			}
		}
		case IChemicalProvider chemical -> {
			if(!chemical.getChemical().isEmptyType()) {
				ing = creator.of(chemical);
				slurries.add(chemical.getChemical());
			}
		}
		case IChemicalProvider[] chemicalz -> {
			List<Chemical> nonEmpty = Arrays.stream(chemicalz).map(IChemicalProvider::getChemical).filter(s->!s.isEmptyType()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = creator.of(nonEmpty.stream());
				slurries.addAll(nonEmpty);
			}
		}
		case JsonElement json -> {
			ing = creator.codec().parse(JsonOps.INSTANCE, json).resultOrPartial(LOGGER::warn).orElse(null);
			// We can't know what chemicales the ingredient can have so assume all
			MekanismAPI.CHEMICAL_REGISTRY.forEach(slurries::add);
		}
		default -> {}
		}
		slurries.remove(MekanismAPI.EMPTY_CHEMICAL);
		return Pair.of(slurries.isEmpty() ? null : ing, slurries);
	}

	public TagKey<Chemical> getChemicalTagKey(ResourceLocation location) {
		return TagKey.create(MekanismAPI.CHEMICAL_REGISTRY_NAME, location);
	}

	public Collection<Chemical> getChemicalTagValues(ResourceLocation location) {
		return MiscHelper.INSTANCE.getTagValues(MekanismAPI.CHEMICAL_REGISTRY_NAME, location);
	}

	public ChemicalStack getPreferredChemicalStack(Iterable<Chemical> collection, int amount) {
		return new ChemicalStack(MiscHelper.INSTANCE.getPreferredEntry(MekanismAPI.CHEMICAL_REGISTRY::getKey, collection).orElse(MekanismAPI.EMPTY_CHEMICAL), amount);
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

	public boolean registerWashingRecipe(ResourceLocation key, Object fluidInput, int fluidInputCount, Object chemicalInput, int chemicalInputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSerializer(key, fluidInput, fluidInputCount, chemicalInput, chemicalInputAmount, output, outputCount));
	}

	public boolean registerCrystallizingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizingRecipeSerializer(key, input, inputCount, output, outputCount));
	}

	public boolean registerDissolutionRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object chemicalInput, int chemicalInputAmount, boolean perTickUsage, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new DissolutionRecipeSerializer(key, itemInput, itemInputCount, chemicalInput, chemicalInputAmount, perTickUsage, output, outputCount));
	}

	public boolean registerPurifyingRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object chemicalInput, int chemicalInputAmount, boolean perTickUsage, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PurifyingRecipeSerializer(key, itemInput, itemInputCount, chemicalInput, chemicalInputAmount, perTickUsage, output, outputCount));
	}

	public boolean registerInjectingRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object chemicalInput, int chemicalInputAmount, boolean perTickUsage, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new InjectingRecipeSerializer(key, itemInput, itemInputCount, chemicalInput, chemicalInputAmount, perTickUsage, output, outputCount));
	}
}
