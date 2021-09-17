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

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.tags.EmptyNamedTag;

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
		return getTagLocation(form, material, "/");
	}

	@Override
	public ResourceLocation getTagLocation(String form, String material, String separator) {
		return createResourceLocation(form+
				(StringUtils.isEmpty(material) ? "" :
					(StringUtils.isEmpty(separator) ? "/" : separator)+material));
	}

	@Override
	public ItemStack getItemStack(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getItemStack(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof ItemStack) {
			return ((ItemStack)obj);
		}
		else if(obj instanceof ItemLike) {
			return new ItemStack((ItemLike)obj, count);
		}
		else if(obj instanceof String) {
			return getPreferredItemStack(getItemTag(new ResourceLocation((String)obj)).getValues(), count);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredItemStack(getItemTag((ResourceLocation)obj).getValues(), count);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredItemStack(((Tag<Item>)obj).getValues(), count);
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
			return Ingredient.of(getItemTag(new ResourceLocation((String)obj)));
		}
		else if(obj instanceof ResourceLocation) {
			return Ingredient.of(getItemTag((ResourceLocation)obj));
		}
		else if(obj instanceof Tag<?>) {
			return Ingredient.of((Tag<Item>)obj);
		}
		else if(obj instanceof ItemStack) {
			return Ingredient.of((ItemStack)obj);
		}
		else if(obj instanceof ItemStack[]) {
			return Ingredient.of((ItemStack[])obj);
		}
		else if(obj instanceof ItemLike) {
			return Ingredient.of((ItemLike)obj);
		}
		else if(obj instanceof ItemLike[]) {
			return Ingredient.of((ItemLike[])obj);
		}
		else if(obj instanceof Ingredient.Value) {
			return Ingredient.fromValues(Stream.of((Ingredient.Value)obj));
		}
		else if(obj instanceof Ingredient.Value[]) {
			return Ingredient.fromValues(Stream.of((Ingredient.Value[])obj));
		}
		else if(obj instanceof JsonElement) {
			return Ingredient.fromJson((JsonElement)obj);
		}
		return Ingredient.EMPTY;
	}

	public Tag<Item> getItemTag(ResourceLocation location) {
		return getTag(Registry.ITEM_REGISTRY, location);
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
		else if(obj instanceof IFluidLike) {
			return new FluidStack(((IFluidLike)obj).asFluid(), amount);
		}
		else if(obj instanceof String) {
			return getPreferredFluidStack(getFluidTag(new ResourceLocation((String)obj)).getValues(), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredFluidStack(getFluidTag((ResourceLocation)obj).getValues(), amount);
		}
		else if(obj instanceof Tag<?>) {
			return getPreferredFluidStack(((Tag<Fluid>)obj).getValues(), amount);
		}
		return FluidStack.EMPTY;
	}

	public Tag<Fluid> getFluidTag(ResourceLocation location) {
		return getTag(Registry.FLUID_REGISTRY, location);
	}

	public FluidStack getPreferredFluidStack(Collection<Fluid> collection, int amount) {
		return new FluidStack(getPreferredEntry(collection).orElse(Fluids.EMPTY), amount);
	}

	public <T> Tag<T> getTag(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
		Tag<T> tag = SerializationTags.getInstance().getOrEmpty(registry).getTag(location);
		return tag != null ? tag : new EmptyNamedTag<>(location);
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
