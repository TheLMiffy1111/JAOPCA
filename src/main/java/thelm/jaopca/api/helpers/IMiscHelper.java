package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public interface IMiscHelper {

	Identifier createIdentifier(String location, String defaultNamespace);

	Identifier createIdentifier(String location);

	Identifier getRecipeKey(String category, String material);

	Identifier getTagLocation(String form, String material);

	Identifier getTagLocation(String form, String material, String separator);

	ItemStackTemplate getItemStackTemplate(Object obj, int count);

	Ingredient getIngredient(Object obj);

	SizedIngredient getSizedIngredient(Object obj, int count);

	TagKey<Item> getItemTagKey(Identifier location);

	Collection<Item> getItemTagValues(Identifier location);

	ItemStackTemplate getPreferredItemStackTemplate(Iterable<Item> iterable, int count);

	FluidStackTemplate getFluidStackTemplate(Object obj, int amount);

	FluidIngredient getFluidIngredient(Object obj);

	SizedFluidIngredient getSizedFluidIngredient(Object obj, int amount);

	TagKey<Fluid> getFluidTagKey(Identifier location);

	Collection<Fluid> getFluidTagValues(Identifier location);

	FluidStackTemplate getPreferredFluidStackTemplate(Iterable<Fluid> iterable, int amount);

	<T> Collection<T> getTagValues(ResourceKey<? extends Registry<T>> registry, Identifier location);

	<T> Collection<T> getTagValues(Identifier registry, Identifier location);

	<T> Optional<T> getPreferredEntry(Function<T, Identifier> keyGetter, Iterable<T> iterable);

	<T> Optional<T> getPreferredEntry(Comparator<T> comparator, Function<T, Identifier> keyGetter, Iterable<T> iterable);

	Comparator<Fluid> flowingFluidComparator();

	<T> Comparator<T> entryPreferenceComparator(Function<T, Identifier> keyGetter);

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

	boolean hasResource(Identifier location);
}
