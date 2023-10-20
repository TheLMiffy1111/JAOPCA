package thelm.jaopca.compat.mekanism;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.mekanism.recipes.CombiningRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.CrushingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.CrystallizingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.DissolutionRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.EnrichingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.InjectingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.PurifyingRecipeSerializer;
import thelm.jaopca.compat.mekanism.recipes.WashingRecipeSerializer;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class MekanismHelper {

	public static final MekanismHelper INSTANCE = new MekanismHelper();

	private MekanismHelper() {}

	public Set<ResourceLocation> getSlurryTags() {
		return ImmutableSortedSet.copyOf(Sets.union(ApiImpl.INSTANCE.getTags("mekanism:slurry"), MekanismDataInjector.getInjectSlurryTags()));
	}

	public ItemStackIngredient getItemStackIngredient(Object obj, int count) {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(obj);
		return ing == EmptyIngredient.INSTANCE ? null : IngredientCreatorAccess.item().from(ing, count);
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
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
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
			ing = creator.from(stack);
			fluids.add(stack.getFluid());
		}
		else if(obj instanceof FluidStack[] stacks) {
			ing = creator.createMulti(Arrays.stream(stacks).map(creator::from).toArray(FluidStackIngredient[]::new));
			Arrays.stream(stacks).map(FluidStack::getFluid).forEach(fluids::add);
		}
		else if(obj instanceof Fluid fluid) {
			ing = creator.from(fluid, amount);
			fluids.add(fluid);
		}
		else if(obj instanceof Fluid[] fluidz) {
			ing = creator.createMulti(Arrays.stream(fluidz).map(f->creator.from(f, amount)).toArray(FluidStackIngredient[]::new));
			Collections.addAll(fluids, fluidz);
		}
		else if(obj instanceof JsonElement) {
			ing = creator.deserialize((JsonElement)obj);
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
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
			gases.addAll(MekanismAPI.gasRegistry().getValues());
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
			ing = creator.from(stack);
			gases.add(stack.getType());
		}
		else if(obj instanceof GasStack[] stacks) {
			ing = creator.createMulti(Arrays.stream(stacks).map(creator::from).toArray(GasStackIngredient[]::new));
			Arrays.stream(stacks).map(GasStack::getType).forEach(gases::add);
		}
		else if(obj instanceof IGasProvider gas) {
			ing = creator.from(gas, amount);
			gases.add(gas.getChemical());
		}
		else if(obj instanceof IGasProvider[] gasez) {
			ing = creator.createMulti(Arrays.stream(gasez).map(g->creator.from(g, amount)).toArray(GasStackIngredient[]::new));
			Arrays.stream(gasez).map(IGasProvider::getChemical).forEach(gases::add);
		}
		else if(obj instanceof JsonElement) {
			ing = creator.deserialize((JsonElement)obj);
			// We can't know what gases the ingredient can have so assume all
			gases.addAll(MekanismAPI.gasRegistry().getValues());
		}
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
			slurries.addAll(MekanismAPI.slurryRegistry().getValues());
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
			ing = creator.from(stack);
			slurries.add(stack.getType());
		}
		else if(obj instanceof SlurryStack[] stacks) {
			ing = creator.createMulti(Arrays.stream(stacks).map(creator::from).toArray(SlurryStackIngredient[]::new));
			Arrays.stream(stacks).map(SlurryStack::getType).forEach(slurries::add);
		}
		else if(obj instanceof ISlurryProvider slurry) {
			ing = creator.from(slurry, amount);
			slurries.add(slurry.getChemical());
		}
		else if(obj instanceof ISlurryProvider[] slurriez) {
			ing = creator.createMulti(Arrays.stream(slurriez).map(s->creator.from(s, amount)).toArray(SlurryStackIngredient[]::new));
			Arrays.stream(slurriez).map(ISlurryProvider::getChemical).forEach(slurries::add);
		}
		else if(obj instanceof JsonElement) {
			ing = creator.deserialize((JsonElement)obj);
			// We can't know what slurries the ingredient can have so assume all
			slurries.addAll(MekanismAPI.slurryRegistry().getValues());
		}
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
		return MiscHelper.INSTANCE.getTagKey(MekanismAPI.gasRegistryName(), location);
	}

	public Collection<Gas> getGasTagValues(ResourceLocation location) {
		return MiscHelper.INSTANCE.getTagValues(MekanismAPI.gasRegistryName(), location);
	}

	public GasStack getPreferredGasStack(Iterable<Gas> collection, int amount) {
		return new GasStack(MiscHelper.INSTANCE.getPreferredEntry(collection).orElse(MekanismAPI.EMPTY_GAS), amount);
	}

	public TagKey<Slurry> getSlurryTagKey(ResourceLocation location) {
		return MiscHelper.INSTANCE.getTagKey(MekanismAPI.slurryRegistryName(), location);
	}

	public Collection<Slurry> getSlurryTagValues(ResourceLocation location) {
		return MiscHelper.INSTANCE.getTagValues(MekanismAPI.slurryRegistryName(), location);
	}

	public SlurryStack getPreferredSlurryStack(Iterable<Slurry> collection, int amount) {
		return new SlurryStack(MiscHelper.INSTANCE.getPreferredEntry(collection).orElse(MekanismAPI.EMPTY_SLURRY), amount);
	}
}
