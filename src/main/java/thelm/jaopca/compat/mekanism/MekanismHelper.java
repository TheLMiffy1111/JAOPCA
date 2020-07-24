package thelm.jaopca.compat.mekanism;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;

import mekanism.api.MekanismAPI;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTags;
import mekanism.api.providers.IGasProvider;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.api.recipes.inputs.GasStackIngredient;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thelm.jaopca.compat.mekanism.recipes.CombiningRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.CrushingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.CrystallizingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.DissolutionRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.EnrichingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.InjectingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.PurifyingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.WashingRecipeSupplier;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.utils.ApiImpl;

public class MekanismHelper {

	public static final MekanismHelper INSTANCE = new MekanismHelper();

	private MekanismHelper() {}

	public Set<ResourceLocation> getGasTags() {
		return ImmutableSortedSet.copyOf(Sets.union(ApiImpl.INSTANCE.getTags("gases"), MekanismDataInjector.getInjectGasTags()));
	}

	public FluidStackIngredient getFluidStackIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidStackIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidStackIngredient) {
			return (FluidStackIngredient)obj;
		}
		else if(obj instanceof String) {
			return FluidStackIngredient.from(makeFluidWrapperTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return FluidStackIngredient.from(makeFluidWrapperTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof Tag<?>) {
			return FluidStackIngredient.from((Tag<Fluid>)obj, amount);
		}
		else if(obj instanceof FluidStack) {
			return FluidStackIngredient.from((FluidStack)obj);
		}
		else if(obj instanceof FluidStack[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((FluidStack[])obj).map(FluidStackIngredient::from).toArray(FluidStackIngredient[]::new));
		}
		else if(obj instanceof Fluid) {
			return FluidStackIngredient.from((Fluid)obj, amount);
		}
		else if(obj instanceof Fluid[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((Fluid[])obj).map(g->FluidStackIngredient.from(g, amount)).toArray(FluidStackIngredient[]::new));
		}
		else if(obj instanceof JsonElement) {
			return FluidStackIngredient.deserialize((JsonElement)obj);
		}
		return FluidStackIngredient.createMulti();
	}

	public GasStackIngredient getGasStackIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getGasStackIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof GasStackIngredient) {
			return (GasStackIngredient)obj;
		}
		else if(obj instanceof String) {
			return GasStackIngredient.from(makeGasWrapperTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return GasStackIngredient.from(makeGasWrapperTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof Tag<?>) {
			return GasStackIngredient.from((Tag<Gas>)obj, amount);
		}
		else if(obj instanceof GasStack) {
			return GasStackIngredient.from((GasStack)obj);
		}
		else if(obj instanceof GasStack[]) {
			return GasStackIngredient.createMulti(Arrays.stream((GasStack[])obj).map(GasStackIngredient::from).toArray(GasStackIngredient[]::new));
		}
		else if(obj instanceof IGasProvider) {
			return GasStackIngredient.from((IGasProvider)obj, amount);
		}
		else if(obj instanceof IGasProvider[]) {
			return GasStackIngredient.createMulti(Arrays.stream((IGasProvider[])obj).map(g->GasStackIngredient.from(g, amount)).toArray(GasStackIngredient[]::new));
		}
		else if(obj instanceof JsonElement) {
			return GasStackIngredient.deserialize((JsonElement)obj);
		}
		return GasStackIngredient.createMulti();
	}

	public GasStack getGasStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getGasStack(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof GasStack) {
			return ((GasStack)obj);
		}
		else if(obj instanceof IGasProvider) {
			return new GasStack((IGasProvider)obj, amount);
		}
		else if(obj instanceof String) {
			return getPreferredGasStack(makeGasWrapperTag(new ResourceLocation((String)obj)).getAllElements(), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredGasStack(makeGasWrapperTag((ResourceLocation)obj).getAllElements(), amount);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredGasStack(((Tag<Gas>)obj).getAllElements(), amount);
		}
		return GasStack.EMPTY;
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSupplier(key, input, inputCount, output, outputCount));
	}

	public boolean registerEnrichingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new EnrichingRecipeSupplier(key, input, inputCount, output, outputCount));
	}

	public boolean registerCombiningRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CombiningRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount));
	}

	public boolean registerWashingRecipe(ResourceLocation key, Object fluidInput, int fluidInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, fluidInput, fluidInputCount, gasInput, gasInputCount, output, outputCount));
	}

	public boolean registerCrystallizingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizingRecipeSupplier(key, input, inputCount, output, outputCount));
	}

	public boolean registerDissolutionRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new DissolutionRecipeSupplier(key, itemInput, itemInputCount, gasInput, gasInputCount, output, outputCount));
	}

	public boolean registerPurifyingRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PurifyingRecipeSupplier(key, itemInput, itemInputCount, gasInput, gasInputCount, output, outputCount));
	}

	public boolean registerInjectingRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object gasInput, int gasInputCount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new InjectingRecipeSupplier(key, itemInput, itemInputCount, gasInput, gasInputCount, output, outputCount));
	}

	public Tag<Gas> makeGasWrapperTag(ResourceLocation location) {
		return new GasTags.Wrapper(location);
	}

	public GasStack getPreferredGasStack(Collection<Gas> collection, int amount) {
		return new GasStack(getPreferredEntry(collection).orElse(MekanismAPI.EMPTY_GAS), amount);
	}

	//Modified from Immersive Engineering
	public <T extends IForgeRegistryEntry<T>> Optional<T> getPreferredEntry(Collection<T> list) {
		T preferredEntry = null;
		int currBest = ConfigHandler.PREFERRED_MODS.size();
		for(T entry : list) {
			ResourceLocation rl = entry.getRegistryName();
			if(rl != null) {
				String modId = rl.getNamespace();
				int idx = ConfigHandler.PREFERRED_MODS.indexOf(modId);
				if(preferredEntry == null || idx >= 0 && idx < currBest) {
					preferredEntry = entry;
					currBest = idx;
				}
			}
		}
		return Optional.ofNullable(preferredEntry);
	}

	public Tag<Fluid> makeFluidWrapperTag(ResourceLocation location) {
		return new FluidTags.Wrapper(location);
	}
}
