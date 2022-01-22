package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
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

	Tag<Item> getItemTag(ResourceLocation location);

	ItemStack getPreferredItemStack(Collection<Item> collection, int count);

	FluidStack getFluidStack(Object obj, int amount);

	Tag<Fluid> getFluidTag(ResourceLocation location);

	FluidStack getPreferredFluidStack(Collection<Fluid> collection, int amount);

	<T> Tag<T> getTag(ResourceKey<? extends Registry<T>> registry, ResourceLocation location);

	<T extends IForgeRegistryEntry<T>> Optional<T> getPreferredEntry(Collection<T> list);

	void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet);

	void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet);

	Predicate<String> configMaterialPredicate();

	Predicate<String> configModulePredicate();
}
