package thelm.jaopca.ingredients;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thelm.jaopca.registries.RegistryHandler;

public class IngredientTypes {

	public static final DeferredHolder<IngredientType<?>, IngredientType<WrappedIngredient>> COMPOUND_INGREDIENT_TYPE =
			RegistryHandler.registerRegistryEntry(NeoForgeRegistries.Keys.INGREDIENT_TYPES, "wrapped", ()->new IngredientType<>(WrappedIngredient.CODEC, WrappedIngredient.CODEC_NONEMPTY));

	public static void init() {}
}
