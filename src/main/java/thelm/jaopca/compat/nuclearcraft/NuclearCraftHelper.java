package thelm.jaopca.compat.nuclearcraft;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;

import igentuman.nc.recipes.ingredient.FluidStackIngredient;
import igentuman.nc.recipes.ingredient.creator.IFluidStackIngredientCreator;
import igentuman.nc.recipes.ingredient.creator.IngredientCreatorAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.nuclearcraft.recipes.CentrifugeRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.CrystallizerRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.IngotFormerRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.LeacherRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.ManufactoryRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.MelterRecipeSerializer;
import thelm.jaopca.compat.nuclearcraft.recipes.PressurizerRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class NuclearCraftHelper {

	public static final NuclearCraftHelper INSTANCE = new NuclearCraftHelper();

	private NuclearCraftHelper() {}

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
			ing = creator.deserialize((JsonElement)obj);
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
		fluids.remove(Fluids.EMPTY);
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public boolean registerLeacherRecipe(ResourceLocation key, Object itemInput, int itemInputCount, Object fluidInput, int fluidInputAmount, Object output, int outputAmount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LeacherRecipeSerializer(key, itemInput, itemInputCount, fluidInput, fluidInputAmount, output, outputAmount, radiation, time, power));
	}

	public boolean registerManufactoryRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ManufactoryRecipeSerializer(key, input, inputCount, output, outputCount, radiation, time, power));
	}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, int inputAmount, Object[] output, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CentrifugeRecipeSerializer(key, input, inputAmount, output, radiation, time, power));
	}

	public boolean registerCrystallizerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizerRecipeSerializer(key, input, inputAmount, output, outputCount, radiation, time, power));
	}

	public boolean registerIngotFormerRecipe(ResourceLocation key, Object input, int inputAmount, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IngotFormerRecipeSerializer(key, input, inputAmount, output, outputCount, radiation, time, power));
	}

	public boolean registerMelterRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputAmount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MelterRecipeSerializer(key, input, inputCount, output, outputAmount, radiation, time, power));
	}

	public boolean registerPressurizerRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double radiation, double time, double power) {
		return ApiImpl.INSTANCE.registerRecipe(key, new PressurizerRecipeSerializer(key, input, inputCount, output, outputCount, radiation, time, power));
	}
}
