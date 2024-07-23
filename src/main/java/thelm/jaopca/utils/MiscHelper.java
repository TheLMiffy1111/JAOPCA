package thelm.jaopca.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.google.gson.JsonElement;

import net.minecraft.client.Minecraft;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thelm.jaopca.api.fluids.IFluidProvider;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.MaterialType;
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
			ItemStack stack = (ItemStack)obj;
			if(!stack.isEmpty()) {
				return stack;
			}
		}
		else if(obj instanceof IItemProvider) {
			Item item = ((IItemProvider)obj).asItem();
			if(item != Items.AIR) {
				return new ItemStack(item, count);
			}
		}
		else if(obj instanceof String) {
			return getPreferredItemStack(getItemTag(new ResourceLocation((String)obj)).getValues(), count);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredItemStack(getItemTag((ResourceLocation)obj).getValues(), count);
		}
		else if(obj instanceof ITag<?>) {
			return getPreferredItemStack(((ITag<Item>)obj).getValues(), count);
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
		else if(obj instanceof ITag<?>) {
			return Ingredient.of((ITag<Item>)obj);
		}
		else if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			if(!stack.isEmpty()) {
				return Ingredient.of(stack);
			}
		}
		else if(obj instanceof ItemStack[]) {
			return Ingredient.of(Arrays.stream((ItemStack[])obj).filter(s->!s.isEmpty()));
		}
		else if(obj instanceof IItemProvider) {
			Item item = ((IItemProvider)obj).asItem();
			if(item != Items.AIR) {
				return Ingredient.of(item);
			}
		}
		else if(obj instanceof IItemProvider[]) {
			return Ingredient.of(Arrays.stream((IItemProvider[])obj).map(IItemProvider::asItem).filter(i->i != Items.AIR).toArray(Item[]::new));
		}
		else if(obj instanceof Ingredient.IItemList) {
			return Ingredient.fromValues(Stream.of((Ingredient.IItemList)obj));
		}
		else if(obj instanceof Ingredient.IItemList[]) {
			return Ingredient.fromValues(Stream.of((Ingredient.IItemList[])obj));
		}
		else if(obj instanceof JsonElement) {
			return Ingredient.fromJson((JsonElement)obj);
		}
		return Ingredient.EMPTY;
	}

	@Override
	public ITag<Item> getItemTag(ResourceLocation location) {
		ITag<Item> tag = TagCollectionManager.getInstance().getItems().getTag(location);
		return tag != null ? tag : new EmptyNamedTag<>(location);
	}

	@Override
	public ItemStack getPreferredItemStack(Collection<Item> collection, int count) {
		return new ItemStack(getPreferredEntry(collection).orElse(Items.AIR), count);
	}

	@Override
	public FluidStack getFluidStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidStack(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidStack) {
			FluidStack stack = (FluidStack)obj;
			if(!stack.isEmpty()) {
				return stack;
			}
		}
		else if(obj instanceof Fluid) {
			Fluid fluid = (Fluid)obj;
			if(fluid != Fluids.EMPTY) {
				return new FluidStack(fluid, amount);
			}
		}
		else if(obj instanceof IFluidProvider) {
			Fluid fluid = ((IFluidProvider)obj).asFluid();
			if(fluid != Fluids.EMPTY) {
				return new FluidStack(fluid, amount);
			}
		}
		else if(obj instanceof String) {
			return getPreferredFluidStack(getFluidTag(new ResourceLocation((String)obj)).getValues(), amount);
		}
		else if(obj instanceof ResourceLocation) {
			return getPreferredFluidStack(getFluidTag((ResourceLocation)obj).getValues(), amount);
		}
		else if(obj instanceof ITag<?>) {
			return getPreferredFluidStack(((ITag<Fluid>)obj).getValues(), amount);
		}
		return FluidStack.EMPTY;
	}

	@Override
	public ITag<Fluid> getFluidTag(ResourceLocation location) {
		ITag<Fluid> tag = TagCollectionManager.getInstance().getFluids().getTag(location);
		return tag != null ? tag : new EmptyNamedTag<>(location);
	}

	@Override
	public FluidStack getPreferredFluidStack(Collection<Fluid> collection, int amount) {
		return new FluidStack(getPreferredEntry(flowingFluidComparator(), collection).orElse(Fluids.EMPTY), amount);
	}

	@Override
	public <T extends IForgeRegistryEntry<T>> Optional<T> getPreferredEntry(Collection<T> list) {
		return list.stream().min(entryPreferenceComparator());
	}

	@Override
	public <T extends IForgeRegistryEntry<T>> Optional<T> getPreferredEntry(Comparator<T> comparator, Collection<T> list) {
		return list.stream().min(comparator.thenComparing(entryPreferenceComparator()));
	}

	private static final Comparator<IForgeRegistryEntry<?>> ENTRY_PREFERENCE_COMPARATOR = (entry1, entry2)->{
		ResourceLocation key1 = entry1.getRegistryName();
		ResourceLocation key2 = entry2.getRegistryName();
		if(key1 == key2) return 0;
		if(key1 == null) return 1;
		if(key2 == null) return -1;
		int index1 = ConfigHandler.PREFERRED_MODS.indexOf(key1.getNamespace());
		int index2 = ConfigHandler.PREFERRED_MODS.indexOf(key2.getNamespace());
		if(index1 == index2) return 0;
		if(index1 == -1) return 1;
		if(index2 == -1) return -1;
		return Integer.compare(index1, index2);
	};
	private static final Comparator<Fluid> FLOWING_FLUID_COMPARATOR = (fluid1, fluid2)->{
		boolean flag1 = fluid1 instanceof FlowingFluid && fluid1 == ((FlowingFluid)fluid1).getFlowing();
		boolean flag2 = fluid2 instanceof FlowingFluid && fluid2 == ((FlowingFluid)fluid2).getFlowing();
		return Boolean.compare(flag1, flag2);
	};

	@Override
	public Comparator<IForgeRegistryEntry<?>> entryPreferenceComparator() {
		return ENTRY_PREFERENCE_COMPARATOR;
	}

	@Override
	public Comparator<Fluid> flowingFluidComparator() {
		return FLOWING_FLUID_COMPARATOR;
	}

	@Override
	public void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet) {
		TreeMultiset<String> list = configList.stream().
				map(s->s.startsWith("*") ? s.toLowerCase(Locale.US) : s).
				collect(Collectors.toCollection(TreeMultiset::create));
		int listCount = list.count("*");
		MaterialHandler.getMaterials().forEach(m->list.add(m.getName(), listCount));
		list.remove("*", listCount);
		for(MaterialType type : MaterialType.values()) {
			int listCount1 = list.count("*"+type.getName());
			MaterialHandler.getMaterials().stream().filter(m->m.getType() == type).forEach(m->list.add(m.getName(), listCount1));
			list.remove("*"+type.getName(), listCount1);
		}
		actualSet.clear();
		list.entrySet().stream().filter(e->(e.getCount() & 1) == 1).map(Multiset.Entry::getElement).forEach(actualSet::add);
	}

	@Override
	public void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet) {
		TreeMultiset<String> list = TreeMultiset.create(configList);
		int listCount = list.count("*");
		ModuleHandler.getModules().forEach(m->list.add(m.getName(), listCount));
		list.remove("*", listCount);
		actualSet.clear();
		list.entrySet().stream().filter(e->(e.getCount() & 1) == 1).map(Multiset.Entry::getElement).forEach(actualSet::add);
	}

	private static final Predicate<String> CONFIG_MATERIAL_PREDICATE = s->s.equals("*") || s.startsWith("*") && MaterialType.fromName(s.substring(1)) != null || MaterialHandler.containsMaterial(s);
	private static final Predicate<String> CONFIG_MODULE_PREDICATE = s->s.equals("*") || ModuleHandler.getModuleMap().containsKey(s);

	@Override
	public Predicate<String> configMaterialPredicate() {
		return CONFIG_MATERIAL_PREDICATE;
	}

	@Override
	public Predicate<String> configModulePredicate() {
		return CONFIG_MODULE_PREDICATE;
	}

	@Override
	public Runnable conditionalRunnable(BooleanSupplier conditionSupplier, Supplier<Runnable> trueRunnable, Supplier<Runnable> falseRunnable) {
		return ()->(conditionSupplier.getAsBoolean() ? trueRunnable : falseRunnable).get().run();
	}

	@Override
	public <T> Supplier<T> conditionalSupplier(BooleanSupplier conditionSupplier, Supplier<Supplier<T>> trueSupplier, Supplier<Supplier<T>> falseSupplier) {
		return ()->(conditionSupplier.getAsBoolean() ? trueSupplier : falseSupplier).get().get();
	}

	@Override
	public boolean hasResource(ResourceLocation location) {
		return DistExecutor.unsafeRunForDist(()->()->Minecraft.getInstance().getResourceManager().hasResource(location), ()->()->false);
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

	public Predicate<String> modVersionNotLoaded(Logger logger) {
		return dep->{
			ModList modList = ModList.get();
			int separatorIndex = dep.lastIndexOf('@');
			String modId = dep.substring(0, separatorIndex == -1 ? dep.length() : separatorIndex);
			String spec = separatorIndex == -1 ? "0" : dep.substring(separatorIndex+1);
			VersionRange versionRange;
			try {
				versionRange = VersionRange.createFromVersionSpec(spec);
			}
			catch(InvalidVersionSpecificationException e) {
				logger.warn("Unable to parse version spec {} for mod id {}", spec, modId, e);
				return true;
			}
			if(modList.isLoaded(modId)) {
				ArtifactVersion version = modList.getModContainerById(modId).get().getModInfo().getVersion();
				if(versionRange.containsVersion(version)) {
					return false;
				}
				else {
					logger.warn("Mod {} in version range {} was requested, was {}", modId, versionRange, version);
					return true;
				}
			}
			return true;
		};
	}

	public boolean classNotExists(String className) {
		try {
			Class.forName(className, false, getClass().getClassLoader());
			return false;
		}
		catch(ClassNotFoundException e) {
			return true;
		}
	}
}
