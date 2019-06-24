package thelm.jaopca.utils;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
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
	public ItemStack getStack(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getStack(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof ItemStack) {
			return ((ItemStack)obj);
		}
		else if(obj instanceof IItemProvider) {
			return new ItemStack((IItemProvider)obj, count);
		}
		else if(obj instanceof String) {
			return getPreferredStack(makeItemWrapperTag(new ResourceLocation((String)obj)).getAllElements(), count);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredStack(makeItemWrapperTag((ResourceLocation)obj).getAllElements(), count);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredStack(((Tag<Item>)obj).getAllElements(), count);
		}
		return null;
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
		else if(obj instanceof JsonElement) {
			return Ingredient.deserialize((JsonElement)obj);
		}
		return null;
	}

	public Tag<Item> makeItemWrapperTag(ResourceLocation location) {
		return new ItemTags.Wrapper(location);
	}

	public ItemStack getPreferredStack(Collection<Item> collection, int count) {
		return new ItemStack(collection.stream().findFirst().orElse(Items.AIR), count);
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
