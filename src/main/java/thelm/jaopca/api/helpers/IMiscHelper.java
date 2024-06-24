package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IMiscHelper {

	ResourceLocation createResourceLocation(String location, String defaultNamespace);

	ResourceLocation createResourceLocation(String location);

	ResourceLocation getTagLocation(String form, String material);

	ResourceLocation getTagLocation(String form, String material, String separator);

	ItemStack getItemStack(Object obj, int count);

	Ingredient getIngredient(Object obj);

	TagKey<Item> getItemTagKey(ResourceLocation location);

	Collection<Item> getItemTagValues(ResourceLocation location);

	ItemStack getPreferredItemStack(Iterable<Item> iterable, int count);

	FluidStack getFluidStack(Object obj, int amount);

	TagKey<Fluid> getFluidTagKey(ResourceLocation location);

	Collection<Fluid> getFluidTagValues(ResourceLocation location);

	FluidStack getPreferredFluidStack(Iterable<Fluid> iterable, int amount);

	<T> TagKey<T> getTagKey(ResourceKey<? extends Registry<T>> registry, ResourceLocation location);

	<T> TagKey<T> getTagKey(ResourceLocation registry, ResourceLocation location);

	<T> Collection<T> getTagValues(ResourceKey<? extends Registry<T>> registry, ResourceLocation location);

	<T> Collection<T> getTagValues(ResourceLocation registry, ResourceLocation location);

	<T> Optional<T> getPreferredEntry(Function<T, ResourceLocation> keyGetter, Iterable<T> iterable);

	<T> Optional<T> getPreferredEntry(Comparator<T> comparator, Function<T, ResourceLocation> keyGetter, Iterable<T> iterable);

	Comparator<Fluid> flowingFluidComparator();

	<T> Comparator<T> entryPreferenceComparator(Function<T, ResourceLocation> keyGetter);

	void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet);

	void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet);

	Ingredient wrapIngredient(Ingredient ing);

	JsonObject serializeItemStack(ItemStack stack);

	JsonObject serializeFluidStack(FluidStack stack);

	Predicate<String> configMaterialPredicate();

	Predicate<String> configModulePredicate();

	Runnable conditionalRunnable(BooleanSupplier conditionSupplier, Supplier<Runnable> trueRunnable, Supplier<Runnable> falseRunnable);

	<T> Supplier<T> conditionalSupplier(BooleanSupplier conditionSupplier, Supplier<Supplier<T>> trueSupplier, Supplier<Supplier<T>> falseSupplier);

	boolean hasResource(ResourceLocation location);
}
