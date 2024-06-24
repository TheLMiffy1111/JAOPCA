package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IMiscHelper {

	ResourceLocation getRecipeKey(String category, String material);

	String getOredictName(String form, String material);

	String getFluidName(String form, String material);

	ItemStack getItemStack(Object obj, int count);

	Ingredient getIngredient(Object obj);

	ItemStack getPreferredItemStack(Iterable<ItemStack> iterable, int count);

	ItemStack resizeItemStack(ItemStack stack, int size);

	FluidStack getFluidStack(Object obj, int amount);

	FluidStack resizeFluidStack(FluidStack stack, int amount);

	Comparator<IForgeRegistryEntry<?>> entryPreferenceComparator();

	Predicate<String> metaItemPredicate();

	ItemStack parseMetaItem(String str);

	String toLowercaseUnderscore(String camelCase);

	void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet);

	void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet);

	Predicate<String> configMaterialPredicate();

	Predicate<String> configModulePredicate();

	Runnable conditionalRunnable(BooleanSupplier conditionSupplier, Supplier<Runnable> trueRunnable, Supplier<Runnable> falseRunnable);

	<T> Supplier<T> conditionalSupplier(BooleanSupplier conditionSupplier, Supplier<Supplier<T>> trueSupplier, Supplier<Supplier<T>> falseSupplier);

	boolean hasResource(ResourceLocation location);
}
