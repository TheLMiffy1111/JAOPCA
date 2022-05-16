package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

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
import net.minecraftforge.registries.IForgeRegistryEntry;

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

	<T extends IForgeRegistryEntry<T>> TagKey<T> getTagKey(ResourceKey<? extends Registry<T>> registry, ResourceLocation location);

	<T extends IForgeRegistryEntry<T>> TagKey<T> getTagKey(ResourceLocation registry, ResourceLocation location);

	<T extends IForgeRegistryEntry<T>> Collection<T> getTagValues(ResourceKey<? extends Registry<T>> registry, ResourceLocation location);

	<T extends IForgeRegistryEntry<T>> Collection<T> getTagValues(ResourceLocation registry, ResourceLocation location);

	<T extends IForgeRegistryEntry<T>> Optional<T> getPreferredEntry(Iterable<T> iterable);

	void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet);

	void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet);

	JsonObject serializeItemStack(ItemStack stack);

	JsonObject serializeFluidStack(FluidStack stack);

	Predicate<String> configMaterialPredicate();

	Predicate<String> configModulePredicate();
}
