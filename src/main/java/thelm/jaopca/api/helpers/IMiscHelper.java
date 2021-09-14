package thelm.jaopca.api.helpers;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IMiscHelper {

	ResourceLocation createResourceLocation(String location, String defaultNamespace);

	ResourceLocation createResourceLocation(String location);

	ResourceLocation getTagLocation(String form, String material);

	ResourceLocation getTagLocation(String form, String material, String separator);

	ItemStack getItemStack(Object obj, int count);

	Ingredient getIngredient(Object obj);

	<T extends IForgeRegistryEntry<T>> Optional<T> getPreferredEntry(Collection<T> list);

	void caclulateMaterialSet(Collection<String> configList, Collection<String> actualSet);

	void caclulateModuleSet(Collection<String> configList, Collection<String> actualSet);

	Predicate<String> configMaterialPredicate();

	Predicate<String> configModulePredicate();
}
