package thelm.jaopca.compat.theurgy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;
import com.klikli_dev.theurgy.content.recipe.ingredient.FluidIngredient;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.theurgy.recipes.IncubationRecipeSerializer;
import thelm.jaopca.compat.theurgy.recipes.LiquefactionRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class TheurgyHelper {

	public static final TheurgyHelper INSTANCE = new TheurgyHelper();

	private TheurgyHelper() {}

	public FluidIngredient getFluidIngredient(Object obj) {
		return getFluidIngredientResolved(obj).getLeft();
	}

	public Pair<FluidIngredient, Set<Fluid>> getFluidIngredientResolved(Object obj) {
		FluidIngredient ing = null;
		Set<Fluid> fluids = new HashSet<>();
		IMiscHelper helper = MiscHelper.INSTANCE;
		if(obj instanceof Supplier<?>) {
			Pair<FluidIngredient, Set<Fluid>> pair = getFluidIngredientResolved(((Supplier<?>)obj).get());
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
			ing = FluidIngredient.ofFluid(helper.getFluidTagKey(location));
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = FluidIngredient.ofFluid(helper.getFluidTagKey(location));
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = FluidIngredient.ofFluid(key);
			fluids.addAll(helper.getFluidTagValues(key.location()));
		}
		else if(obj instanceof FluidStack stack) {
			if(!stack.isEmpty()) {
				ing = FluidIngredient.ofFluid(stack);
				fluids.add(stack.getFluid());
			}
		}
		else if(obj instanceof FluidStack[] stacks) {
			List<FluidStack> nonEmpty = Arrays.stream(stacks).filter(s->!s.isEmpty()).toList();
			if(!nonEmpty.isEmpty()) {
				ing = FluidIngredient.ofFluid(nonEmpty.stream());
				nonEmpty.stream().map(FluidStack::getFluid).forEach(fluids::add);
			}
		}
		else if(obj instanceof Fluid fluid) {
			if(fluid != Fluids.EMPTY) {
				ing = FluidIngredient.ofFluid(fluid);
				fluids.add(fluid);
			}
		}
		else if(obj instanceof Fluid[] fluidz) {
			List<Fluid> nonEmpty = Arrays.stream(fluidz).filter(f->f != Fluids.EMPTY).toList();
			if(!nonEmpty.isEmpty()) {
				ing = FluidIngredient.ofFluid(nonEmpty.toArray(Fluid[]::new));
				fluids.addAll(nonEmpty);
			}
		}
		else if(obj instanceof IFluidLike fluid) {
			if(fluid.asFluid() != Fluids.EMPTY) {
				ing = FluidIngredient.ofFluid(fluid.asFluid());
				fluids.add(fluid.asFluid());
			}
		}
		else if(obj instanceof IFluidLike[] fluidz) {
			List<Fluid> nonEmpty = Arrays.stream(fluidz).map(IFluidLike::asFluid).filter(f->f != Fluids.EMPTY).toList();
			if(!nonEmpty.isEmpty()) {
				ing = FluidIngredient.ofFluid(nonEmpty.toArray(Fluid[]::new));
				fluids.addAll(nonEmpty);
			}
		}
		else if(obj instanceof JsonElement) {
			ing = FluidIngredient.fromJson((JsonElement)obj);
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
		fluids.remove(Fluids.EMPTY);
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public boolean registerLiquefactionRecipe(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new LiquefactionRecipeSerializer(key, itemInput, fluidInput, fluidInputAmount, output, outputCount, time));
	}

	public boolean registerIncubationRecipe(ResourceLocation key, Object mercury, Object salt, Object sulfur, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new IncubationRecipeSerializer(key, mercury, salt, sulfur, output, outputCount, time));
	}
}
