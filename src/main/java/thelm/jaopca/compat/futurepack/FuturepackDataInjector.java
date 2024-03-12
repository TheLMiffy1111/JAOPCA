package thelm.jaopca.compat.futurepack;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import futurepack.common.recipes.Json2Recipes;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.config.ConfigHandler;

public class FuturepackDataInjector {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeBasedTable<String, ResourceLocation, IRecipeSerializer> FP_RECIPES_INJECT = TreeBasedTable.create();

	public static boolean registerRecipe(String type, ResourceLocation key, IRecipeSerializer recipeSupplier) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeSupplier);
		if(ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		return FP_RECIPES_INJECT.row(type).putIfAbsent(key, recipeSupplier) != null;
	}

	private FuturepackDataInjector() {}

	public static void setupInjectRecipes() {
		try {
			Field recipeManagersField = Json2Recipes.class.getDeclaredField("recipeManagers");
			recipeManagersField.setAccessible(true);
			Map<String, Consumer<JsonArray>> recipeManagers = (Map<String, Consumer<JsonArray>>)recipeManagersField.get(null);
			FP_RECIPES_INJECT.rowMap().forEach((type, map)->{
				if(recipeManagers.containsKey(type)) {
					recipeManagers.computeIfPresent(type, (t, consumer)->recipeArray->{
						JsonArray recipesToInject = new JsonArray();
						map.forEach((key, supplier)->{
							JsonElement recipe = null;
							try {
								recipe = supplier.get();
							}
							catch(IllegalArgumentException e) {
								LOGGER.debug("Recipe with ID {} received invalid arguments: {}", key, e.getMessage());
								return;
							}
							catch(Throwable e) {
								LOGGER.warn("Recipe with ID {} errored", key, e);
								return;
							}
							if(recipe == null) {
								LOGGER.debug("Recipe with ID {} returned null", key);
								return;
							}
							recipesToInject.add(recipe);
							LOGGER.debug("Injected recipe with ID {}", key);
						});
						recipeArray.addAll(recipesToInject);
						LOGGER.info("Injected {} recipes of type {}, {} recipes total", recipesToInject.size(), type, recipeArray.size());
						consumer.accept(recipeArray);
					});
				}
				else {
					LOGGER.error("Futurepack recipe manager of type {} does not exist", type);
				}
			});
		}
		catch(Exception e) {
			LOGGER.error("Unable to access Futurepack recipe manager list", e);
		}
	}
}
