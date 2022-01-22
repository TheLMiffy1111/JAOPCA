package thelm.jaopca.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
import thelm.jaopca.api.ingredients.CompoundIngredientObject;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.ingredients.DifferenceIngredient;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.ingredients.IntersectionIngredient;
import thelm.jaopca.ingredients.UnionIngredient;
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
		ItemStack ret = ItemStack.EMPTY;
		if(obj instanceof Supplier<?>) {
			ret = getItemStack(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof ItemStack) {
			ret = ((ItemStack)obj);
		}
		else if(obj instanceof ItemLike) {
			ret = new ItemStack((ItemLike)obj, count);
		}
		else if(obj instanceof String) {
			ret = getPreferredItemStack(getItemTag(new ResourceLocation((String)obj)).getValues(), count);
		}
		else if(obj instanceof ResourceLocation) {
			ret = getPreferredItemStack(getItemTag((ResourceLocation)obj).getValues(), count);
		}
		else if(obj instanceof Tag<?>) {
			ret = getPreferredItemStack(((Tag<Item>)obj).getValues(), count);
		}
		return ret.isEmpty() ? ItemStack.EMPTY : ret;
	}

	@Override
	public Ingredient getIngredient(Object obj) {
		Ingredient ret = EmptyIngredient.INSTANCE;
		if(obj instanceof Supplier<?>) {
			ret = getIngredient(((Supplier<?>)obj).get());
		}
		else if(obj instanceof CompoundIngredientObject) {
			CompoundIngredientObject cObj = (CompoundIngredientObject)obj;
			List<Ingredient> ings = Arrays.stream(cObj.ingredients()).map(this::getIngredient).collect(Collectors.toList());
			if(ings.size() == 1) {
				ret = ings.get(0);
			}
			else if(ings.size() > 1) {
				ret = switch(cObj.type()) {
				case UNION -> UnionIngredient.of(ings);
				case INTERSECTION -> IntersectionIngredient.of(ings);
				case DIFFERENCE -> DifferenceIngredient.of(ings.get(0), UnionIngredient.of(ings.subList(1, ings.size())));
				};
			}
		}
		else if(obj instanceof Ingredient) {
			ret = (Ingredient)obj;
		}
		else if(obj instanceof String) {
			Tag<Item> tag = getItemTag(new ResourceLocation((String)obj));
			if(!(tag instanceof EmptyNamedTag<?>)) {
				ret = Ingredient.of(tag);
			}
		}
		else if(obj instanceof ResourceLocation) {
			Tag<Item> tag = getItemTag((ResourceLocation)obj);
			if(!(tag instanceof EmptyNamedTag<?>)) {
				ret = Ingredient.of(tag);
			}
		}
		else if(obj instanceof Tag<?> && !(obj instanceof EmptyNamedTag<?>)) {
			ret = Ingredient.of((Tag<Item>)obj);
		}
		else if(obj instanceof ItemStack) {
			ret = Ingredient.of((ItemStack)obj);
		}
		else if(obj instanceof ItemStack[]) {
			ret = Ingredient.of((ItemStack[])obj);
		}
		else if(obj instanceof ItemLike) {
			ret = Ingredient.of((ItemLike)obj);
		}
		else if(obj instanceof ItemLike[]) {
			ret = Ingredient.of((ItemLike[])obj);
		}
		else if(obj instanceof Ingredient.Value) {
			ret = Ingredient.fromValues(Stream.of((Ingredient.Value)obj));
		}
		else if(obj instanceof Ingredient.Value[]) {
			ret = Ingredient.fromValues(Stream.of((Ingredient.Value[])obj));
		}
		else if(obj instanceof JsonElement) {
			ret = Ingredient.fromJson((JsonElement)obj);
		}
		return ret.isEmpty() ? EmptyIngredient.INSTANCE : ret;
	}

	@Override
	public Tag<Item> getItemTag(ResourceLocation location) {
		return getTag(Registry.ITEM_REGISTRY, location);
	}

	@Override
	public ItemStack getPreferredItemStack(Collection<Item> collection, int count) {
		return new ItemStack(getPreferredEntry(collection).orElse(Items.AIR), count);
	}

	@Override
	public FluidStack getFluidStack(Object obj, int amount) {
		FluidStack ret = FluidStack.EMPTY;
		if(obj instanceof Supplier<?>) {
			ret = getFluidStack(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidStack) {
			ret = ((FluidStack)obj);
		}
		else if(obj instanceof Fluid) {
			ret = new FluidStack((Fluid)obj, amount);
		}
		else if(obj instanceof IFluidLike) {
			ret = new FluidStack(((IFluidLike)obj).asFluid(), amount);
		}
		else if(obj instanceof String) {
			ret = getPreferredFluidStack(getFluidTag(new ResourceLocation((String)obj)).getValues(), amount);
		}
		else if(obj instanceof ResourceLocation) {
			ret = getPreferredFluidStack(getFluidTag((ResourceLocation)obj).getValues(), amount);
		}
		else if(obj instanceof Tag<?>) {
			ret = getPreferredFluidStack(((Tag<Fluid>)obj).getValues(), amount);
		}
		return ret.isEmpty() ? FluidStack.EMPTY : ret;
	}

	@Override
	public Tag<Fluid> getFluidTag(ResourceLocation location) {
		return getTag(Registry.FLUID_REGISTRY, location);
	}

	@Override
	public FluidStack getPreferredFluidStack(Collection<Fluid> collection, int amount) {
		return new FluidStack(getPreferredEntry(collection).orElse(Fluids.EMPTY), amount);
	}

	@Override
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
