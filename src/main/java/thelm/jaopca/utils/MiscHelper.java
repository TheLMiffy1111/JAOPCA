package thelm.jaopca.utils;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.helpers.IMiscHelper;

public class MiscHelper implements IMiscHelper {

	public static final MiscHelper INSTANCE = new MiscHelper();

	private MiscHelper() {}

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	@Override
	public ResourceLocation getTagLocation(String form, String material) {
		return new ResourceLocation("forge", form+(StringUtils.isEmpty(material) ? "" : '/'+material));
	}

	@Override
	public ItemStack getItemStack(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getItemStack(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof ItemStack) {
			return ((ItemStack)obj);
		}
		else if(obj instanceof IItemProvider) {
			return new ItemStack((IItemProvider)obj, count);
		}
		else if(obj instanceof String) {
			return getPreferredItemStack(makeItemWrapperTag(new ResourceLocation((String)obj)).getAllElements(), count);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredItemStack(makeItemWrapperTag((ResourceLocation)obj).getAllElements(), count);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredItemStack(((Tag<Item>)obj).getAllElements(), count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public Ingredient getIngredient(Object obj) {
		if(obj instanceof Supplier<?>) {
			return getIngredient(((Supplier<?>)obj).get());
		}
		else if(obj instanceof Ingredient) {
			return (Ingredient)obj;
		}
		else if(obj instanceof String) {
			return Ingredient.fromTag(makeItemWrapperTag(new ResourceLocation((String)obj)));
		}
		else if(obj instanceof ResourceLocation) {
			return Ingredient.fromTag(makeItemWrapperTag((ResourceLocation)obj));
		}
		else if(obj instanceof Tag<?>) {
			return Ingredient.fromTag((Tag<Item>)obj);
		}
		else if(obj instanceof ItemStack) {
			return Ingredient.fromStacks((ItemStack)obj);
		}
		else if(obj instanceof ItemStack[]) {
			return Ingredient.fromStacks((ItemStack[])obj);
		}
		else if(obj instanceof IItemProvider) {
			return Ingredient.fromItems((IItemProvider)obj);
		}
		else if(obj instanceof IItemProvider[]) {
			return Ingredient.fromItems((IItemProvider[])obj);
		}
		else if(obj instanceof Ingredient.IItemList) {
			return Ingredient.fromItemListStream(Stream.of((Ingredient.IItemList)obj));
		}
		else if(obj instanceof Ingredient.IItemList[]) {
			return Ingredient.fromItemListStream(Stream.of((Ingredient.IItemList[])obj));
		}
		else if(obj instanceof JsonElement) {
			return Ingredient.deserialize((JsonElement)obj);
		}
		return Ingredient.EMPTY;
	}

	public Tag<Item> makeItemWrapperTag(ResourceLocation location) {
		return new ItemTags.Wrapper(location);
	}

	public ItemStack getPreferredItemStack(Collection<Item> collection, int count) {
		return new ItemStack(collection.stream().findFirst().orElse(Items.AIR), count);
	}

	public FluidStack getFluidStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidStack(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidStack) {
			return ((FluidStack)obj);
		}
		else if(obj instanceof Fluid) {
			return new FluidStack((Fluid)obj, amount);
		}
		else if(obj instanceof String) {
			return getPreferredFluidStack(makeFluidWrapperTag(new ResourceLocation((String)obj)).getAllElements(), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredFluidStack(makeFluidWrapperTag((ResourceLocation)obj).getAllElements(), amount);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredFluidStack(((Tag<Fluid>)obj).getAllElements(), amount);
		}
		return FluidStack.EMPTY;
	}

	public Tag<Fluid> makeFluidWrapperTag(ResourceLocation location) {
		return new FluidTags.Wrapper(location);
	}

	public FluidStack getPreferredFluidStack(Collection<Fluid> collection, int amount) {
		return new FluidStack(collection.stream().findFirst().orElse(Fluids.EMPTY), amount);
	}

	public <T> Future<T> submitAsyncTask(Callable<T> task) {
		return executor.submit(task);
	}

	public Future<?> submitAsyncTask(Runnable task) {
		return executor.submit(task);
	}

	public int squareColorDifference(int color1, int color2) {
		int diffR = (color1<<16&0xFF)-(color2<<16&0xFF);
		int diffG = (color1<< 8&0xFF)-(color2<< 8&0xFF);
		int diffB = (color1    &0xFF)-(color2    &0xFF);
		return diffR*diffR+diffG*diffG+diffB*diffB;
	}
}
