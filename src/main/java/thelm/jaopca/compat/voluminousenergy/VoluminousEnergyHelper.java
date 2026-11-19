package thelm.jaopca.compat.voluminousenergy;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.veteam.voluminousenergy.util.recipe.VERecipeCodecs.RegistryIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
		Set<Item> items = new HashSet<>();
		if(obj instanceof Supplier<?>) {
			Pair<RegistryIngredient, Set<Item>> pair = getRegistryIngredientResolved(((Supplier<?>)obj).get(), count);
			ing = pair.getLeft();
			items.addAll(pair.getRight());
		}
		else if(obj instanceof RegistryIngredient) {
			ing = (RegistryIngredient)obj;
			// We can't know what items the ingredient can have so assume all
			BuiltInRegistries.ITEM.forEach(items::add);
		}
		else if(obj instanceof String) {
			ResourceLocation location = ResourceLocation.parse((String)obj);
			ing = new RegistryIngredient(location.toString(), "", count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(location));
		}
		else if(obj instanceof ResourceLocation location) {
			ing = new RegistryIngredient(location.toString(), "", count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(location));
		}
		else if(obj instanceof TagKey key) {
			ing = new RegistryIngredient(key.location().toString(), "", count);
			items.addAll(MiscHelper.INSTANCE.getItemTagValues(key.location()));
		}
		else if(obj instanceof ItemStack stack) {
			if(!stack.isEmpty()) {
				ing = new RegistryIngredient("", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), count);
				items.add(stack.getItem());
			}
		}
		else if(obj instanceof ItemLike item) {
			if(item.asItem() != Items.AIR) {
				ing = new RegistryIngredient("", BuiltInRegistries.ITEM.getKey(item.asItem()).toString(), count);
				items.add(item.asItem());
			}
		}
		items.remove(Items.AIR);
		return Pair.of(items.isEmpty() ? null : ing, items);
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, Object secondOutput, int secondOutputCount, float secondChance, int time, int minExperience, int maxExperience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount, secondOutput, secondOutputCount, secondChance, time, minExperience, maxExperience));
	}

	public boolean registerCrushingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time, int minExperience, int maxExperience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrushingRecipeSerializer(key, input, inputCount, output, outputCount, time, minExperience, maxExperience));
	}

	public boolean registerCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompressingRecipeSerializer(key, input, inputCount, output, outputCount, time));
	}

	public boolean registerImplosionCompressingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ImplosionCompressingRecipeSerializer(key, input, inputCount, output, outputCount, time));
	}
}
