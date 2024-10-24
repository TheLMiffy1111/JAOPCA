package thelm.jaopca.compat.create;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
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

	private CreateHelper() {}

	public FluidIngredient getFluidIngredient(Object obj, int amount) {
		return getFluidIngredientResolved(obj, amount).getLeft();
	}

	public Pair<FluidIngredient, Set<Fluid>> getFluidIngredientResolved(Object obj, int amount) {
		FluidIngredient ing = null;
		Set<Fluid> fluids = new HashSet<>();
		IMiscHelper helper = MiscHelper.INSTANCE;
		if(obj instanceof Supplier<?>) {
			Pair<FluidIngredient, Set<Fluid>> pair = getFluidIngredientResolved(((Supplier<?>)obj).get(), amount);
			ing = pair.getLeft();
			fluids.addAll(pair.getRight());
		}
		else if(obj instanceof FluidIngredient) {
			ing = (FluidIngredient)obj;
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
		else if(obj instanceof String) {
			ResourceLocation location = new ResourceLocation((String)obj);
			ing = FluidIngredient.fromTag(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = FluidIngredient.fromTag(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = FluidIngredient.fromTag(key, amount);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		else if(obj instanceof FluidStack stack) {
			if(!stack.isEmpty()) {
				ing = FluidIngredient.fromFluidStack(stack);
				fluids.add(stack.getFluid());
			}
		}
		else if(obj instanceof Fluid fluid) {
			if(fluid != Fluids.EMPTY) {
				ing = FluidIngredient.fromFluid(fluid, amount);
				fluids.add(fluid);
			}
		}
		else if(obj instanceof IFluidLike fluid) {
			if(fluid.asFluid() != Fluids.EMPTY) {
				ing = FluidIngredient.fromFluid(fluid.asFluid(), amount);
				fluids.add(fluid.asFluid());
			}
		}
		else if(obj instanceof JsonElement) {
			ing = FluidIngredient.deserialize((JsonElement)obj);
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
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
