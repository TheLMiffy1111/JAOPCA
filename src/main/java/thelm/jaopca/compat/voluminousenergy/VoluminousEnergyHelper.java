package thelm.jaopca.compat.voluminousenergy;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.RegistryIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import thelm.jaopca.compat.voluminousenergy.recipes.CompressingRecipeSerializer;
import thelm.jaopca.compat.voluminousenergy.recipes.CrushingRecipeSerializer;
import thelm.jaopca.compat.voluminousenergy.recipes.ImplosionCompressingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class VoluminousEnergyHelper {

	public static final VoluminousEnergyHelper INSTANCE = new VoluminousEnergyHelper();

	private VoluminousEnergyHelper() {}

	public RegistryIngredient getRegistryIngredient(Object obj, int count) {
		return getRegistryIngredientResolved(obj, count).getLeft();
	}

	// Voluminous Energy ingredients only accept a single item or item tag
	public Pair<RegistryIngredient, Set<Item>> getRegistryIngredientResolved(Object obj, int count) {
		RegistryIngredient ing = null;
		Set<Item> items = new LinkedHashSet<>();
		switch(obj) {
		case Supplier<?> supplier -> {
			Pair<RegistryIngredient, Set<Item>> pair = getRegistryIngredientResolved(supplier.get(), count);
			ing = pair.getLeft();
			items.addAll(pair.getRight());
		}
		case RegistryIngredient registryIng -> {
			ing = registryIng;
			// We can't know what items the ingredient can have so assume all
			BuiltInRegistries.ITEM.forEach(items::add);
		}
		case String str -> {
			Identifier location = Identifier.parse(str);
			ing = new RegistryIngredient(location.toString(), "", count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(location));
		}
		case Identifier location -> {
			ing = new RegistryIngredient(location.toString(), "", count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(location));
		}
		case TagKey<?> key -> {
			ing = new RegistryIngredient(key.location().toString(), "", count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(key.location()));
		}
		case Holder<?> holder -> {
			if(holder.isBound() && holder.value() instanceof Item item && item != Items.AIR) {
				ing = new RegistryIngredient("", BuiltInRegistries.ITEM.getKey(item).toString(), count);
				items.add(item);
			}
		}
		case ItemLike item -> {
			if(item.asItem() != Items.AIR) {
				ing = new RegistryIngredient("", BuiltInRegistries.ITEM.getKey(item.asItem()).toString(), count);
				items.add(item.asItem());
			}
		}
		case null, default -> {}
		}
		items.remove(Items.AIR);
		return Pair.of(items.isEmpty() ? null : ing, items);
	}

	public boolean registerCrushingRecipe(Identifier key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time, int minExperience, int maxExperience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, time, minExperience, maxExperience));
	}

	public boolean registerCrushingRecipe(Identifier key, Object input, int inputCount, Object output, int outputCount, int time, int minExperience, int maxExperience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount, time, minExperience, maxExperience));
	}

	public boolean registerCompressingRecipe(Identifier key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, input, inputCount, output, outputCount, time));
	}

	public boolean registerImplosionCompressingRecipe(Identifier key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ImplosionCompressingRecipeSerializer(key, input, inputCount, output, outputCount, time));
	}
}
