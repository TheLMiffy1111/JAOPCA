package thelm.jaopca.compat.bloodmagic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.bloodmagic.recipes.ARCRecipeSerializer;
import thelm.jaopca.compat.bloodmagic.recipes.AlchemyTableRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;
import wayoftime.bloodmagic.recipe.helper.FluidStackIngredient;

public class BloodMagicHelper {

	public static final BloodMagicHelper INSTANCE = new BloodMagicHelper();

	private BloodMagicHelper() {}

	public FluidStackIngredient getFluidStackIngredient(Object obj, int amount) {
		return getFluidStackIngredientResolved(obj, amount).getLeft();
	}

	public Pair<FluidStackIngredient, Set<Fluid>> getFluidStackIngredientResolved(Object obj, int amount) {
		FluidStackIngredient ing = null;
		Set<Fluid> fluids = new HashSet<>();
		IMiscHelper helper = MiscHelper.INSTANCE;
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
			ing = FluidStackIngredient.from(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = FluidStackIngredient.from(helper.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = FluidStackIngredient.from(key, amount);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		else if(obj instanceof FluidStack stack) {
			ing = FluidStackIngredient.from(stack);
			fluids.add(stack.getFluid());
		}
		else if(obj instanceof FluidStack[] stacks) {
			ing = FluidStackIngredient.createMulti(Arrays.stream(stacks).map(FluidStackIngredient::from).toArray(FluidStackIngredient[]::new));
			Arrays.stream(stacks).map(FluidStack::getFluid).forEach(fluids::add);
		}
		else if(obj instanceof Fluid fluid) {
			ing = FluidStackIngredient.from(fluid, amount);
			fluids.add(fluid);
		}
		else if(obj instanceof Fluid[] fluidz) {
			ing = FluidStackIngredient.createMulti(Arrays.stream(fluidz).map(f->FluidStackIngredient.from(f, amount)).toArray(FluidStackIngredient[]::new));
			Collections.addAll(fluids, fluidz);
		}
		else if(obj instanceof JsonElement) {
			ing = FluidStackIngredient.deserialize((JsonElement)obj);
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public boolean registerAlchemyTableRecipe(ResourceLocation key, Object[] input, Object output, int count, int cost, int time, int minTier) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlchemyTableRecipeSerializer(key, input, output, count, cost, time, minTier));
	}

	public boolean registerARCRecipe(ResourceLocation key, Object input, int inputCount, Object tool, Object fluidInput, int fluidInputAmount, Object[] output, Object fluidOutput, int fluidOutputAmount, boolean consumeInput) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ARCRecipeSerializer(key, input, inputCount, tool, fluidInput, fluidInputAmount, output, fluidOutput, fluidOutputAmount, consumeInput));
	}

	public boolean registerARCRecipe(ResourceLocation key, Object input, int inputCount, Object tool, Object[] output, boolean consumeInput) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ARCRecipeSerializer(key, input, inputCount, tool, output, consumeInput));
	}
}
