package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IMiscHelper {

	ResourceLocation createResourceLocation(String location, String defaultNamespace);

	ResourceLocation createResourceLocation(String location);

	ResourceLocation getTagLocation(String form, String material);

	ResourceLocation getTagLocation(String form, String material, String separator);

	ItemStack getItemStack(Object obj, int count);

	Ingredient getIngredient(Object obj);

	ITag<Item> getItemTag(ResourceLocation location);

	ItemStack getPreferredItemStack(Collection<Item> iterable, int count);

	FluidStack getFluidStack(Object obj, int amount);

	ITag<Fluid> getFluidTag(ResourceLocation location);

	FluidStack getPreferredFluidStack(Collection<Fluid> iterable, int amount);

	<T extends IForgeRegistryEntry<T>> Optional<T> getPreferredEntry(Collection<T> list);

	void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet);

	void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet);

	Predicate<String> configMaterialPredicate();

	Predicate<String> configModulePredicate();

	boolean hasResource(ResourceLocation location);
}
