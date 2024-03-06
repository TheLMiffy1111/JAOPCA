package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

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

	<T> Collection<T> getTagValues(ResourceKey<? extends Registry<T>> registry, ResourceLocation location);

	<T> Collection<T> getTagValues(ResourceLocation registry, ResourceLocation location);

	<T> Optional<T> getPreferredEntry(Function<T, ResourceLocation> keyGetter, Iterable<T> iterable);

	void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet);

	void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet);

	Ingredient wrapIngredient(Ingredient ing);

	JsonElement serializeIngredient(Ingredient ing);

	JsonObject serializeItemStack(ItemStack stack);

	JsonObject serializeFluidStack(FluidStack stack);

	JsonElement serializeRecipe(Recipe<?> recipe);

	<T> JsonElement serialize(Codec<T> codec, T obj);

	Predicate<String> configMaterialPredicate();

	Predicate<String> configModulePredicate();

	Runnable conditionalRunnable(BooleanSupplier conditionSupplier, Supplier<Runnable> trueRunnable, Supplier<Runnable> falseRunnable);

	<T> Supplier<T> conditionalSupplier(BooleanSupplier conditionSupplier, Supplier<Supplier<T>> trueSupplier, Supplier<Supplier<T>> falseSupplier);

	boolean hasResource(ResourceLocation location);
}
