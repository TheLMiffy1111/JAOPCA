package thelm.jaopca.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultiset;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
import thelm.jaopca.api.fluids.IFluidLike;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.ingredients.CompoundIngredientObject;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.ingredients.EmptyIngredient;
import thelm.jaopca.ingredients.WrappedIngredient;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;

public class MiscHelper implements IMiscHelper {

	public static final MiscHelper INSTANCE = new MiscHelper();

	private MiscHelper() {}

	private final ExecutorService executor = Executors.newSingleThreadExecutor(r->new Thread(r, "JAOPCA Executor Thread"));

	private TagManager tagManager;
	private List<TagManager.LoadResult<?>> lastTagResults = List.of();
	private Map<ResourceKey<? extends Registry<?>>, SetMultimap<ResourceLocation, Object>> tagMap = new TreeMap<>();

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

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
		ItemStack ret = getPreferredItemStack(getIngredientResolved(obj).getRight(), count);
		return ret.isEmpty() ? ItemStack.EMPTY : ret;
	}

	@Override
	public Ingredient getIngredient(Object obj) {
		return getIngredientResolved(obj).getLeft();
	}

	public Pair<Ingredient, Set<Item>> getIngredientResolved(Object obj) {
		Ingredient ing = EmptyIngredient.INSTANCE;
		Set<Item> items = new LinkedHashSet<>();
		if(obj instanceof Supplier<?>) {
			Pair<Ingredient, Set<Item>> pair = getIngredientResolved(((Supplier<?>)obj).get());
			ing = pair.getLeft();
			items.addAll(pair.getRight());
		}
		else if(obj instanceof CompoundIngredientObject cObj) {
			List<Pair<Ingredient, Set<Item>>> ings = Arrays.stream(cObj.ingredients()).map(this::getIngredientResolved).toList();
			if(ings.size() == 1) {
				Pair<Ingredient, Set<Item>> pair = ings.get(0);
				ing = pair.getLeft();
				items.addAll(pair.getRight());
			}
			else if(ings.size() > 1) {
				switch(cObj.type()) {
				case UNION -> {
					if(ings.stream().allMatch(p->p.getRight().isEmpty())) {
						break;
					}
					ing = CompoundIngredient.of(ings.stream().map(Pair::getLeft).toArray(Ingredient[]::new));
					items.addAll(ings.stream().map(Pair::getRight).reduce(new HashSet<>(), (s1, s2)->{
						s1.addAll(s2);
						return s1;
					}));
				}
				case INTERSECTION -> {
					if(ings.stream().anyMatch(p->p.getRight().isEmpty())) {
						break;
					}
					ing = IntersectionIngredient.of(ings.stream().map(Pair::getLeft).toArray(Ingredient[]::new));
					items.addAll(ings.stream().map(Pair::getRight).reduce(new HashSet<>(ForgeRegistries.ITEMS.getValues()), (s1, s2)->{
						s1.retainAll(s2);
						return s1;
					}));
				}
				case DIFFERENCE -> {
					Pair<Ingredient, Set<Item>> firstPair = ings.get(0);
					if(firstPair.getRight().isEmpty()) {
						break;
					}
					ing = DifferenceIngredient.of(firstPair.getLeft(), CompoundIngredient.of(ings.stream().skip(1).map(Pair::getLeft).toArray(Ingredient[]::new)));
					items.addAll(firstPair.getRight());
					items.removeAll(ings.stream().skip(1).map(Pair::getRight).reduce(new HashSet<>(), (s1, s2)->{
						s1.addAll(s2);
						return s1;
					}));
				}
				}
			}
		}
		else if(obj instanceof Ingredient) {
			ing = (Ingredient)obj;
			// We can't know what items the ingredient can have so assume all
			items.addAll(ForgeRegistries.ITEMS.getValues());
		}
		else if(obj instanceof String) {
			ResourceLocation location = new ResourceLocation((String)obj);
			ing = Ingredient.of(getItemTagKey(location));
			items.addAll(getItemTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = Ingredient.of(getItemTagKey(location));
			items.addAll(getItemTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = Ingredient.of(key);
			items.addAll(getItemTagValues(key.location()));
		}
		else if(obj instanceof ItemStack stack) {
			ing = Ingredient.of(stack);
			items.add(stack.getItem());
		}
		else if(obj instanceof ItemStack[] stacks) {
			ing = Ingredient.of(stacks);
			Arrays.stream(stacks).map(ItemStack::getItem).forEach(items::add);
		}
		else if(obj instanceof ItemLike item) {
			ing = Ingredient.of(item);
			items.add(item.asItem());
		}
		else if(obj instanceof ItemLike[] itemz) {
			ing = Ingredient.of(itemz);
			Arrays.stream(itemz).map(ItemLike::asItem).forEach(items::add);
		}
		else if(obj instanceof Ingredient.Value) {
			ing = Ingredient.fromValues(Stream.of((Ingredient.Value)obj));
			// We can't know what items the ingredient can have so assume all
			items.addAll(ForgeRegistries.ITEMS.getValues());
		}
		else if(obj instanceof Ingredient.Value[]) {
			ing = Ingredient.fromValues(Stream.of((Ingredient.Value[])obj));
			// We can't know what items the ingredient can have so assume all
			items.addAll(ForgeRegistries.ITEMS.getValues());
		}
		else if(obj instanceof JsonElement) {
			ing = Ingredient.fromJson((JsonElement)obj);
			// We can't know what items the ingredient can have so assume all
			items.addAll(ForgeRegistries.ITEMS.getValues());
		}
		return Pair.of(items.isEmpty() ? EmptyIngredient.INSTANCE : ing, items);
	}

	@Override
	public TagKey<Item> getItemTagKey(ResourceLocation location) {
		return getTagKey(Registry.ITEM_REGISTRY, location);
	}

	@Override
	public Collection<Item> getItemTagValues(ResourceLocation location) {
		return getTagValues(Registry.ITEM_REGISTRY, location);
	}

	@Override
	public ItemStack getPreferredItemStack(Iterable<Item> iterable, int count) {
		return new ItemStack(getPreferredEntry(ForgeRegistries.ITEMS::getKey, iterable).orElse(Items.AIR), count);
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
			ret = getPreferredFluidStack(getFluidTagValues(new ResourceLocation((String)obj)), amount);
		}
		else if(obj instanceof ResourceLocation) {
			ret = getPreferredFluidStack(getFluidTagValues((ResourceLocation)obj), amount);
		}
		else if(obj instanceof TagKey<?>) {
			ret = getPreferredFluidStack(getFluidTagValues(((TagKey<Fluid>)obj).location()), amount);
		}
		return ret.isEmpty() ? FluidStack.EMPTY : ret;
	}

	@Override
	public TagKey<Fluid> getFluidTagKey(ResourceLocation location) {
		return getTagKey(Registry.FLUID_REGISTRY, location);
	}

	@Override
	public Collection<Fluid> getFluidTagValues(ResourceLocation location) {
		return getTagValues(Registry.FLUID_REGISTRY, location);
	}

	@Override
	public FluidStack getPreferredFluidStack(Iterable<Fluid> iterable, int amount) {
		return new FluidStack(getPreferredEntry(ForgeRegistries.FLUIDS::getKey, iterable).orElse(Fluids.EMPTY), amount);
	}

	@Override
	public <T> TagKey<T> getTagKey(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
		return RegistryManager.ACTIVE.getRegistry(registry).tags().createTagKey(location);
	}

	@Override
	public <T> TagKey<T> getTagKey(ResourceLocation registry, ResourceLocation location) {
		return RegistryManager.ACTIVE.<T>getRegistry(registry).tags().createTagKey(location);
	}

	@Override
	public <T> Collection<T> getTagValues(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
		if(tagManager == null) {
			throw new IllegalStateException("Tag manager not initialized.");
		}
		if(tagManager.getResult() != lastTagResults) {
			lastTagResults = tagManager.getResult();
			tagMap.clear();
			if(lastTagResults.isEmpty()) {
				throw new IllegalStateException("Tags have not been loaded yet.");
			}
			lastTagResults.forEach(result->{
				SetMultimap<ResourceLocation, Object> map = tagMap.computeIfAbsent(result.key(), k->MultimapBuilder.treeKeys().linkedHashSetValues().build());
				result.tags().forEach((loc, tag)->{
					tag.forEach(holder->map.put(loc, holder.value()));
				});
			});
		}
		return Collections2.transform(tagMap.getOrDefault(registry, ImmutableSetMultimap.of()).asMap().getOrDefault(location, Set.of()), o->(T)o);
	}

	@Override
	public <T> Collection<T> getTagValues(ResourceLocation registry, ResourceLocation location) {
		return getTagValues(RegistryManager.ACTIVE.<T>getRegistry(registry).getRegistryKey(), location);
	}

	//Modified from Immersive Engineering
	@Override
	public <T> Optional<T> getPreferredEntry(Function<T, ResourceLocation> keyGetter, Iterable<T> iterable) {
		T preferredEntry = null;
		int currBest = ConfigHandler.PREFERRED_MODS.size();
		for(T entry : iterable) {
			ResourceLocation rl = keyGetter.apply(entry);
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

	@Override
	public Ingredient wrapIngredient(Ingredient ing) {
		return WrappedIngredient.of(ing);
	}

	@Override
	public JsonObject serializeItemStack(ItemStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
		json.addProperty("count", stack.getCount());
		if(stack.hasTag()) {
			json.addProperty("nbt", stack.getTag().toString());
		}
		return json;
	}

	@Override
	public JsonObject serializeFluidStack(FluidStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
		json.addProperty("amount", stack.getAmount());
		if(stack.hasTag()) {
			json.addProperty("nbt", stack.getTag().toString());
		}
		return json;
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
		return DistExecutor.unsafeRunForDist(()->()->Minecraft.getInstance().getResourceManager().getResource(location).isPresent(), ()->()->false);
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
}
