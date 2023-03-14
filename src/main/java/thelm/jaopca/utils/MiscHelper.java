package thelm.jaopca.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.primitives.Ints;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.fluids.IFluidProvider;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemProvider;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;

public class MiscHelper implements IMiscHelper {

	public static final MiscHelper INSTANCE = new MiscHelper();

	private MiscHelper() {}

	private final ExecutorService executor = Executors.newSingleThreadExecutor(r->new Thread(r, "JAOPCA Executor Thread"));

	@Override
	public String getRecipeKey(String category, String material) {
		if(StringUtils.contains(category, ':')) {
			return category+'.'+toLowercaseUnderscore(material);
		}
		return "jaopca:"+category+'.'+toLowercaseUnderscore(material);
	}

	@Override
	public String getOredictName(String form, String material) {
		return form+material;
	}

	@Override
	public String getFluidName(String form, String material) {
		if(form.startsWith(".")) {
			form = form.substring(1);
			return material.toLowerCase(Locale.US)+(form.isEmpty() ? "" : ".")+form;
		}
		return form+(form.isEmpty() ? "" : "_")+toLowercaseUnderscore(material);
	}

	@Override
	public List<ItemStack> getItemStacks(Object obj, int count, boolean allowWildcard) {
		List<ItemStack> ret = new ArrayList<>();
		if(obj instanceof Supplier<?>) {
			ret.addAll(getItemStacks(((Supplier<?>)obj).get(), count, allowWildcard));
		}
		else if(obj instanceof List<?>) {
			for(Object o : (List<?>)obj) {
				ret.addAll(getItemStacks(o, count, allowWildcard));
			}
		}
		else if(obj instanceof String) {
			for(ItemStack stack : OreDictionary.getOres((String)obj, false)) {
				if(allowWildcard || stack.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
					ret.add(resizeItemStack(stack, count));
				}
				else {
					ret.add(new ItemStack(stack.getItem(), count));
				}
			}
		}
		else if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			if(stack.getItem() != null) {
				if(allowWildcard || stack.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
					ret.add(resizeItemStack(stack, count));
				}
				else {
					ret.add(new ItemStack(stack.getItem(), count));
				}
			}
		}
		else if(obj instanceof Item) {
			ret.add(new ItemStack((Item)obj, count));
		}
		else if(obj instanceof Block) {
			ret.add(new ItemStack((Block)obj, count));
		}
		else if(obj instanceof IItemProvider) {
			ret.add(new ItemStack(((IItemProvider)obj).asItem(), count));
		}
		return ret;
	}

	@Override
	public ItemStack getItemStack(Object obj, int count, boolean allowWildcard) {
		return getPreferredItemStack(getItemStacks(obj, count, allowWildcard), count);
	}

	@Override
	public ItemStack getPreferredItemStack(Iterable<ItemStack> iterable, int count) {
		ItemStack preferredEntry = null;
		int currBest = ConfigHandler.PREFERRED_MODS.size();
		for(ItemStack entry : iterable) {
			String name = Item.itemRegistry.getNameForObject(entry.getItem());
			if(name != null) {
				String modId = name.split(":")[0];
				int idx = ConfigHandler.PREFERRED_MODS.indexOf(modId);
				if(preferredEntry == null || idx >= 0 && idx < currBest) {
					preferredEntry = entry;
					if(idx >= 0) {
						currBest = idx;
					}
				}
			}
		}
		return resizeItemStack(preferredEntry, count);
	}

	@Override
	public ItemStack resizeItemStack(ItemStack stack, int count) {
		if(stack != null) {
			ItemStack ret = stack.copy();
			ret.stackSize = count;
			return ret;
		}
		return null;
	}

	@Override
	public FluidStack getFluidStack(Object obj, int amount) {
		if(obj instanceof Supplier<?>) {
			return getFluidStack(((Supplier<?>)obj).get(), amount);
		}
		else if(obj instanceof FluidStack) {
			return resizeFluidStack((FluidStack)obj, amount);
		}
		else if(obj instanceof Fluid) {
			return new FluidStack((Fluid)obj, amount);
		}
		else if(obj instanceof IFluidProvider) {
			return new FluidStack(((IFluidProvider)obj).asFluid(), amount);
		}
		else if(obj instanceof String) {
			return FluidRegistry.getFluidStack((String)obj, amount);
		}
		return null;
	}

	@Override
	public FluidStack resizeFluidStack(FluidStack stack, int amount) {
		if(stack != null) {
			FluidStack ret = stack.copy();
			ret.amount = amount;
			return ret;
		}
		return null;
	}

	private static final Predicate<String> META_ITEM_PREDICATE = s->Item.itemRegistry.containsKey(s.split("@(?=\\d*$)")[0]);

	@Override
	public Predicate<String> metaItemPredicate() {
		return META_ITEM_PREDICATE;
	}

	@Override
	public ItemStack parseMetaItem(String str) {
		String[] split = str.split("@(?=\\d*$)");
		int meta = 0;
		if(split.length == 2) {
			meta = Optional.ofNullable(Ints.tryParse(split[1])).orElse(0);
		}
		return new ItemStack((Item)Item.itemRegistry.getObject(split[0]), 1, meta);
	}

	@Override
	public String toLowercaseUnderscore(String camelCase) {
		if(StringUtils.isEmpty(camelCase)) {
			return "";
		}
		return Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(camelCase)).
				map(s->s.toLowerCase(Locale.US)).collect(Collectors.joining("_"));
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
		return ()->{
			if(conditionSupplier.getAsBoolean()) trueRunnable.get().run();
			else falseRunnable.get().run();
		};
	}

	@Override
	public <T> Supplier<T> conditionalSupplier(BooleanSupplier conditionSupplier, Supplier<Supplier<T>> trueSupplier, Supplier<Supplier<T>> falseSupplier) {
		return ()->conditionSupplier.getAsBoolean() ? trueSupplier.get().get() : falseSupplier.get().get();
	}

	@Override
	public boolean hasResource(ResourceLocation location) {
		return conditionalSupplier(FMLCommonHandler.instance().getSide()::isClient, ()->()->{
			try {
				return Minecraft.getMinecraft().getResourceManager().getResource(location) != null;
			}
			catch(Exception e) {
				return false;
			}
		}, ()->()->false).get();
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
			Loader modLoader = Loader.instance();
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
			if(Loader.isModLoaded(modId)) {
				ArtifactVersion version = new DefaultArtifactVersion(modLoader.getIndexedModList().get(modId).getDisplayVersion());
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

	public <T> List<List<T>> guavaCartesianProduct(List<? extends List<? extends T>> lists) {
		try {
			Method method = Lists.class.getDeclaredMethod("cartesianProduct", List.class);
			method.setAccessible(true);
			return (List<List<T>>)method.invoke(null, lists);
		}
		catch(Exception e) {
			return Collections.emptyList();
		}
	}
}
