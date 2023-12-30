package thelm.jaopca.compat.embers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;
import com.rekindled.embers.recipe.FluidIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.embers.recipes.MeltingRecipeSerializer;
import thelm.jaopca.compat.embers.recipes.StampingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class EmbersHelper {

	public static final EmbersHelper INSTANCE = new EmbersHelper();

	private EmbersHelper() {}

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
			ing = FluidIngredient.of(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = FluidIngredient.of(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = FluidIngredient.of(key, amount);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		else if(obj instanceof FluidStack stack) {
			ing = FluidIngredient.of(stack);
			fluids.add(stack.getFluid());
		}
		else if(obj instanceof FluidStack[] stacks) {
			ing = FluidIngredient.of(Arrays.stream(stacks).map(FluidIngredient::of).toArray(FluidIngredient[]::new));
			Arrays.stream(stacks).map(FluidStack::getFluid).forEach(fluids::add);
		}
		else if(obj instanceof Fluid fluid) {
			ing = FluidIngredient.of(fluid, amount);
			fluids.add(fluid);
		}
		else if(obj instanceof Fluid[] fluidz) {
			ing = FluidIngredient.of(Arrays.stream(fluidz).map(f->FluidIngredient.of(f, amount)).toArray(FluidIngredient[]::new));
			Collections.addAll(fluids, fluidz);
		}
		else if(obj instanceof JsonElement) {
			ing = FluidIngredient.deserialize((JsonElement)obj, "");
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, Object output, int outputAmount, Object secondOutput, int secondOutputAmount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, input, output, outputAmount, secondOutput, secondOutputAmount));
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, Object output, int outputAmount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, input, output, outputAmount));
	}
	
	public boolean registerStampingRecipe(ResourceLocation key, Object stamp, Object itemInput, Object fluidInput, int fluidInputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampingRecipeSerializer(key, stamp, itemInput, fluidInput, fluidInputAmount, output, outputCount));
	}
	
	public boolean registerStampingRecipe(ResourceLocation key, Object stamp, Object itemInput, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampingRecipeSerializer(key, stamp, itemInput, output, outputCount));
	}
	
	public boolean registerStampingRecipe(ResourceLocation key, Object stamp, Object fluidInput, int fluidInputAmount, Object output, int outputCount) {
		return ApiImpl.INSTANCE.registerRecipe(key, new StampingRecipeSerializer(key, stamp, fluidInput, fluidInputAmount, output, outputCount));
	}
}
