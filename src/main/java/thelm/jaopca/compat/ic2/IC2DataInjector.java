package thelm.jaopca.compat.ic2;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.data.DataInjector;

public class IC2DataInjector {

	private IC2DataInjector() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeBasedTable<String, ResourceLocation, IRecipeSerializer> IC2_RECIPES_INJECT = TreeBasedTable.create();

	public static void init() {
		try {
			DataInjector.registerReloadInjector(Class.forName("ic2.core.platform.recipes.helpers.IC2RecipeLoader"), IC2DataInjector::injectRecipes);
		}
		catch(ClassNotFoundException e) {
			LOGGER.warn("IC2 Classic recipe loader not found", e);
		}
	}

	public static boolean registerRecipe(String type, ResourceLocation key, IRecipeSerializer recipeSupplier) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeSupplier);
		if(ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		return IC2_RECIPES_INJECT.row(type).putIfAbsent(key, recipeSupplier) != null;
	}

	public static void injectRecipes(Object object) {
		Map<ResourceLocation, JsonElement> recipeMap = (Map<ResourceLocation, JsonElement>)object;
		Map<ResourceLocation, JsonElement> recipesToInject = new TreeMap<>();
		IC2_RECIPES_INJECT.cellSet().forEach(cell->{
			String type = cell.getRowKey();
			ResourceLocation key = cell.getColumnKey();
			IRecipeSerializer supplier = cell.getValue();
			ResourceLocation mapKey = new ResourceLocation(key.getPath(), type+'/'+key.getPath());
			if(recipeMap.containsKey(mapKey)) {
				LOGGER.debug("Duplicate IC2 recipe ignored with type {} and ID {}", type, key);
				return;
			}
			JsonElement recipe = null;
			try {
				recipe = supplier.get();
			}
			catch(IllegalArgumentException e) {
				LOGGER.debug("IC2 recipe with type {} and ID {} received invalid arguments: {}", type, key, e.getMessage());
				return;
			}
			catch(Throwable e) {
				LOGGER.warn("IC2 recipe with type {} and ID {} errored", type, key, e);
				return;
			}
			if(recipe == null) {
				LOGGER.debug("IC2 recipe with type {} and ID {} returned null", type, key);
				return;
			}
			recipesToInject.put(mapKey, recipe);
			LOGGER.debug("Injected IC2 recipe with type {} and ID {}", type, key);
		});
		recipesToInject.forEach(recipeMap::putIfAbsent);
		LOGGER.info("Injected {} IC2 recipes, {} IC2 recipes total", recipesToInject.size(), recipeMap.size());
	}
}
