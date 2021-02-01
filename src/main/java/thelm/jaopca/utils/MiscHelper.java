package thelm.jaopca.utils;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
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
import net.minecraftforge.registries.IForgeRegistryEntry;
import thelm.jaopca.api.fluids.IFluidProvider;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;

public class MiscHelper implements IMiscHelper {

	public static final MiscHelper INSTANCE = new MiscHelper();

	private MiscHelper() {}

	private final ExecutorService executor = Executors.newSingleThreadExecutor(r->new Thread(r, "JAOPCA Executor Thread"));

	@Override
	public ResourceLocation createResourceLocation(String location, String defaultNamespace) {
		if(StringUtils.contains(location, ':')) {
			return new ResourceLocation(location);
		}
		else {
			return new ResourceLocation(defaultNamespace, location);
		}
	}

	@Override
	public ResourceLocation createResourceLocation(String location) {
		return createResourceLocation(location, "forge");
	}

	@Override
	public ResourceLocation getTagLocation(String form, String material) {
		return createResourceLocation(form+(StringUtils.isEmpty(material) ? "" : '/'+material));
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
		return new ItemStack(getPreferredEntry(collection).orElse(Items.AIR), count);
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
		else if(obj instanceof IFluidProvider) {
			return new FluidStack(((IFluidProvider)obj).asFluid(), amount);
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
		return new FluidStack(getPreferredEntry(collection).orElse(Fluids.EMPTY), amount);
	}

	//Modified from Immersive Engineering
	@Override
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
					if(idx >= 0) {
						currBest = idx;
					}
				}
			}
		}
		return Optional.ofNullable(preferredEntry);
	}

	@Override
	public void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet) {
		TreeMultiset<String> list = TreeMultiset.create(configList);
		int listCount = list.count("*");
		MaterialHandler.getMaterialMap().keySet().forEach(s->list.add(s, listCount));
		list.remove("*", listCount);
		actualSet.clear();
		list.entrySet().stream().filter(e->(e.getCount() & 1) == 1).map(Multiset.Entry::getElement).forEach(actualSet::add);
	}

	@Override
	public void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet) {
		TreeMultiset<String> list = TreeMultiset.create(configList);
		int listCount = list.count("*");
		ModuleHandler.getModuleMap().keySet().forEach(s->list.add(s, listCount));
		list.remove("*", listCount);
		actualSet.clear();
		list.entrySet().stream().filter(e->(e.getCount() & 1) == 1).map(Multiset.Entry::getElement).forEach(actualSet::add);
	}

	private static final Predicate<String> CONFIG_MATERIAL_PREDICATE = s->"*".equals(s) || MaterialHandler.containsMaterial(s);
	private static final Predicate<String> CONFIG_MODULE_PREDICATE = s->"*".equals(s) || ModuleHandler.getModuleMap().containsKey(s);

	@Override
	public Predicate<String> configMaterialPredicate() {
		return CONFIG_MATERIAL_PREDICATE;
	}

	@Override
	public Predicate<String> configModulePredicate() {
		return CONFIG_MODULE_PREDICATE;
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
