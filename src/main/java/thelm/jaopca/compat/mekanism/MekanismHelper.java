package thelm.jaopca.compat.mekanism;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.providers.IGasProvider;
import mekanism.api.providers.ISlurryProvider;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.api.recipes.inputs.chemical.SlurryStackIngredient;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidProvider;
import thelm.jaopca.compat.mekanism.recipes.CombiningRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.CrushingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.CrystallizingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.DissolutionRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.EnrichingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.InjectingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.PurifyingRecipeSupplier;
import thelm.jaopca.compat.mekanism.recipes.WashingRecipeSupplier;
import thelm.jaopca.tags.EmptyNamedTag;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class MekanismHelper {

	public static final MekanismHelper INSTANCE = new MekanismHelper();

	private MekanismHelper() {}

	public Set<ResourceLocation> getSlurryTags() {
		return ApiImpl.INSTANCE.getTags("slurries");
	}

	public FluidStackIngredient getFluidStackIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidStackIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidStackIngredient) {
			return (FluidStackIngredient)obj;
		}
		else if(obj instanceof String) {
			return FluidStackIngredient.from(MiscHelper.INSTANCE.getFluidTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return FluidStackIngredient.from(MiscHelper.INSTANCE.getFluidTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof ITag<?>) {
			return FluidStackIngredient.from((ITag<Fluid>)obj, amount);
		}
		else if(obj instanceof FluidStack) {
			FluidStack stack = (FluidStack)obj;
			if(!stack.isEmpty()) {
				return FluidStackIngredient.from(stack);
			}
		}
		else if(obj instanceof FluidStack[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((FluidStack[])obj).filter(s->!s.isEmpty()).map(FluidStackIngredient::from).toArray(FluidStackIngredient[]::new));
		}
		else if(obj instanceof Fluid) {
			if(obj != Fluids.EMPTY) {
				return FluidStackIngredient.from((Fluid)obj, amount);
			}
		}
		else if(obj instanceof Fluid[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((Fluid[])obj).filter(f->f != Fluids.EMPTY).map(f->FluidStackIngredient.from(f, amount)).toArray(FluidStackIngredient[]::new));
		}
		else if(obj instanceof IFluidProvider) {
			Fluid fluid = ((IFluidProvider)obj).asFluid();
			if(fluid != Fluids.EMPTY) {
				return FluidStackIngredient.from(fluid, amount);
			}
		}
		else if(obj instanceof IFluidProvider[]) {
			return FluidStackIngredient.createMulti(Arrays.stream((IFluidProvider[])obj).map(IFluidProvider::asFluid).map(g->FluidStackIngredient.from(g, amount)).toArray(FluidStackIngredient[]::new));
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
			return GasStackIngredient.from(getGasTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return GasStackIngredient.from(getGasTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof ITag<?>) {
			return GasStackIngredient.from((ITag<Gas>)obj, amount);
		}
		else if(obj instanceof GasStack) {
			GasStack stack = (GasStack)obj;
			if(!stack.isEmpty()) {
				return GasStackIngredient.from(stack);
			}
		}
		else if(obj instanceof GasStack[]) {
			return GasStackIngredient.createMulti(Arrays.stream((GasStack[])obj).filter(s->!s.isEmpty()).map(GasStackIngredient::from).toArray(GasStackIngredient[]::new));
		}
		else if(obj instanceof IGasProvider) {
			Gas gas = ((IGasProvider)obj).getChemical();
			if(!gas.isEmptyType()) {
				return GasStackIngredient.from(gas, amount);
			}
		}
		else if(obj instanceof IGasProvider[]) {
			return GasStackIngredient.createMulti(Arrays.stream((IGasProvider[])obj).map(IGasProvider::getChemical).filter(g->!g.isEmptyType()).map(g->GasStackIngredient.from(g, amount)).toArray(GasStackIngredient[]::new));
		}
		else if(obj instanceof JsonElement) {
			return GasStackIngredient.deserialize((JsonElement)obj);
		}
		return GasStackIngredient.createMulti();
	}

	public SlurryStackIngredient getSlurryStackIngredient(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getSlurryStackIngredient(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof SlurryStackIngredient) {
			return (SlurryStackIngredient)obj;
		}
		else if(obj instanceof String) {
			return SlurryStackIngredient.from(getSlurryTag(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return SlurryStackIngredient.from(getSlurryTag((ResourceLocation)obj), amount);
		}
		else if(obj instanceof ITag<?>) {
			return SlurryStackIngredient.from((ITag<Slurry>)obj, amount);
		}
		else if(obj instanceof SlurryStack) {
			SlurryStack stack = (SlurryStack)obj;
			if(!stack.isEmpty()) {
				return SlurryStackIngredient.from(stack);
			}
		}
		else if(obj instanceof SlurryStack[]) {
			return SlurryStackIngredient.createMulti(Arrays.stream((SlurryStack[])obj).filter(s->!s.isEmpty()).map(SlurryStackIngredient::from).toArray(SlurryStackIngredient[]::new));
		}
		else if(obj instanceof ISlurryProvider) {
			Slurry slurry = ((ISlurryProvider)obj).getChemical();
			if(!slurry.isEmptyType()) {
				return SlurryStackIngredient.from((ISlurryProvider)obj, amount);
			}
		}
		else if(obj instanceof ISlurryProvider[]) {
			return SlurryStackIngredient.createMulti(Arrays.stream((ISlurryProvider[])obj).map(ISlurryProvider::getChemical).filter(s->!s.isEmptyType()).map(g->SlurryStackIngredient.from(g, amount)).toArray(SlurryStackIngredient[]::new));
		}
		else if(obj instanceof JsonElement) {
			return SlurryStackIngredient.deserialize((JsonElement)obj);
		}
		return SlurryStackIngredient.createMulti();
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
			return getPreferredGasStack(getGasTag(new ResourceLocation((String)obj)).getValues(), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredGasStack(getGasTag((ResourceLocation)obj).getValues(), amount);
		}
		else if(obj instanceof ITag<?>) {
			return getPreferredGasStack(((ITag<Gas>)obj).getValues(), amount);
		}
		return GasStack.EMPTY;
	}

	public SlurryStack getSlurryStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getSlurryStack(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof SlurryStack) {
			return ((SlurryStack)obj);
		}
		else if(obj instanceof ISlurryProvider) {
			return new SlurryStack((ISlurryProvider)obj, amount);
		}
		else if(obj instanceof String) {
			return getPreferredSlurryStack(getSlurryTag(new ResourceLocation((String)obj)).getValues(), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredSlurryStack(getSlurryTag((ResourceLocation)obj).getValues(), amount);
		}
		else if(obj instanceof ITag<?>) {
			return getPreferredSlurryStack(((ITag<Slurry>)obj).getValues(), amount);
		}
		return SlurryStack.EMPTY;
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

	public ITag<Gas> getGasTag(ResourceLocation location) {
		ITag<Gas> tag = ChemicalTags.GAS.getCollection().getTag(location);
		return tag != null ? tag : new EmptyNamedTag<>(location);
	}

	public GasStack getPreferredGasStack(Collection<Gas> collection, int amount) {
		return new GasStack(MiscHelper.INSTANCE.getPreferredEntry(collection).orElse(MekanismAPI.EMPTY_GAS), amount);
	}

	public ITag<Slurry> getSlurryTag(ResourceLocation location) {
		ITag<Slurry> tag = ChemicalTags.SLURRY.getCollection().getTag(location);
		return tag != null ? tag : new EmptyNamedTag<>(location);
	}

	public SlurryStack getPreferredSlurryStack(Collection<Slurry> collection, int amount) {
		return new SlurryStack(MiscHelper.INSTANCE.getPreferredEntry(collection).orElse(MekanismAPI.EMPTY_SLURRY), amount);
	}
}
