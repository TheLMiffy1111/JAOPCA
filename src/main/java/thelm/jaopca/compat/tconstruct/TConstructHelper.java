package thelm.jaopca.compat.tconstruct;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.compat.tconstruct.recipes.CastingBasinRecipeSerializer;
import thelm.jaopca.compat.tconstruct.recipes.CastingTableRecipeSerializer;
import thelm.jaopca.compat.tconstruct.recipes.MeltingRecipeSerializer;
import thelm.jaopca.compat.tconstruct.recipes.OreMeltingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class TConstructHelper {

	public static final TConstructHelper INSTANCE = new TConstructHelper();

	private TConstructHelper() {}

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
			ing = FluidIngredient.of(MiscHelper.INSTANCE.getFluidTagKey(location), amount);
			fluids.addAll(helper.getFluidTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = FluidIngredient.of(MiscHelper.INSTANCE.getFluidTagKey(location), amount);
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
			ing = FluidIngredient.of(Arrays.stream(fluidz).<FluidIngredient>map(g->FluidIngredient.of(g, amount)).toArray(FluidIngredient[]::new));
			Collections.addAll(fluids, fluidz);
		}
		else if(obj instanceof JsonElement) {
			ing = FluidIngredient.LOADABLE.convert((JsonElement)obj, "ingredient");
			// We can't know what fluids the ingredient can have so assume all
			fluids.addAll(ForgeRegistries.FLUIDS.getValues());
		}
		return Pair.of(fluids.isEmpty() ? null : ing, fluids);
	}

	public ItemOutput getItemOutput(Object obj, int count) {
		return getItemOutputResolved(obj, count).getLeft();
	}

	public Pair<ItemOutput, Set<Item>> getItemOutputResolved(Object obj, int count) {
		ItemOutput out = ItemOutput.fromStack(ItemStack.EMPTY);
		Set<Item> items = new HashSet<>();
		if(obj instanceof Supplier<?>) {
			Pair<ItemOutput, Set<Item>> pair = getItemOutputResolved(((Supplier<?>)obj).get(), count);
			out = pair.getLeft();
			items.addAll(pair.getRight());
		}
		else if(obj instanceof ItemOutput) {
			out = ((ItemOutput)obj);
			// We can't know what items the ingredient can have so assume all
			items.addAll(ForgeRegistries.ITEMS.getValues());
		}
		else if(obj instanceof ItemStack stack) {
			out = ItemOutput.fromStack(stack);
			items.add(stack.getItem());
		}
		else if(obj instanceof ItemLike item) {
			out = ItemOutput.fromItem(item, count);
			items.add(item.asItem());
		}
		else if(obj instanceof String) {
			ResourceLocation location = new ResourceLocation((String)obj);
			out = ItemOutput.fromTag(MiscHelper.INSTANCE.getItemTagKey(location), count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			out = ItemOutput.fromTag(MiscHelper.INSTANCE.getItemTagKey(location), count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			out = ItemOutput.fromTag(key, count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(key.location()));
		}
		return Pair.of(items.isEmpty() ? null : out, items);
	}

	public boolean registerOreMeltingRecipe(ResourceLocation key, String group, Object input, Object output, int outputAmount, int rate, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, Object... byproducts) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreMeltingRecipeSerializer(key, group, input, output, outputAmount, rate, temperature, time, byproducts));
	}

	public boolean registerOreMeltingRecipe(ResourceLocation key, Object input, Object output, int outputAmount, int rate, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, Object... byproducts) {
		return ApiImpl.INSTANCE.registerRecipe(key, new OreMeltingRecipeSerializer(key, input, output, outputAmount, rate, temperature, time, byproducts));
	}

	public boolean registerMeltingRecipe(ResourceLocation key, String group, Object input, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, Object... byproducts) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, group, input, output, outputAmount, temperature, time, byproducts));
	}

	public boolean registerMeltingRecipe(ResourceLocation key, Object input, Object output, int outputAmount, ToIntFunction<FluidStack> temperature, ToIntFunction<FluidStack> time, Object... byproducts) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MeltingRecipeSerializer(key, input, output, outputAmount, temperature, time, byproducts));
	}

	public boolean registerCastingTableRecipe(ResourceLocation key, String group, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingTableRecipeSerializer(key, group, cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots));
	}

	public boolean registerCastingTableRecipe(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingTableRecipeSerializer(key, cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots));
	}

	public boolean registerCastingBasinRecipe(ResourceLocation key, String group, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingBasinRecipeSerializer(key, group, cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots));
	}

	public boolean registerCastingBasinRecipe(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, int outputCount, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CastingBasinRecipeSerializer(key, cast, input, inputAmount, output, outputCount, time, consumeCast, switchSlots));
	}
}
