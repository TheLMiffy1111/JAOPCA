package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public interface IMiscHelper {

	String getRecipeKey(String category, String material);

	String getOredictName(String form, String material);

	String getFluidName(String form, String material);

	List<ItemStack> getItemStacks(Object obj, int count, boolean allowWildcard);

	ItemStack getItemStack(Object obj, int count, boolean allowWildcard);

	ItemStack getPreferredItemStack(Iterable<ItemStack> iterable, int count);

	ItemStack resizeItemStack(ItemStack stack, int size);

	FluidStack getFluidStack(Object obj, int amount);

	FluidStack resizeFluidStack(FluidStack stack, int amount);

	Comparator<Item> itemPreferenceComparator();

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
