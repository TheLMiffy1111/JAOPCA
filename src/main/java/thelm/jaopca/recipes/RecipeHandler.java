package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedSet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import thelm.jaopca.api.recipes.IRecipeAction;

public class RecipeHandler {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<ResourceLocation, IRecipeAction> EARLY_RECIPE_ACTIONS = new TreeMap<>();
	private static final TreeMap<ResourceLocation, IRecipeAction> RECIPE_ACTIONS = new TreeMap<>();
	private static final TreeMap<ResourceLocation, IRecipeAction> LATE_RECIPE_ACTIONS = new TreeMap<>();
	private static final TreeSet<ResourceLocation> EXECUTED_RECIPE_ACTIONS = new TreeSet<>();

	public static boolean registerEarlyRecipe(ResourceLocation key, IRecipeAction recipeAction) {
		if(Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
			return false;
		}
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeAction);
		return EARLY_RECIPE_ACTIONS.putIfAbsent(key, recipeAction) == null;
	}

	public static boolean registerRecipe(ResourceLocation key, IRecipeAction recipeAction) {
		if(Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
			return false;
		}
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeAction);
		return RECIPE_ACTIONS.putIfAbsent(key, recipeAction) == null;
	}

	public static boolean registerLateRecipe(ResourceLocation key, IRecipeAction recipeAction) {
		if(Loader.instance().hasReachedState(LoaderState.AVAILABLE)) {
			return false;
		}
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeAction);
		return LATE_RECIPE_ACTIONS.putIfAbsent(key, recipeAction) == null;
	}

	public static Set<ResourceLocation> getRegisteredRecipes() {
		return ImmutableSortedSet.<ResourceLocation>naturalOrder().
				addAll(EARLY_RECIPE_ACTIONS.keySet()).
				addAll(RECIPE_ACTIONS.keySet()).
				addAll(LATE_RECIPE_ACTIONS.keySet()).
				addAll(EXECUTED_RECIPE_ACTIONS).build();
	}

	public static void registerEarlyRecipes() {
		AtomicInteger recipeCount = new AtomicInteger(0);
		EARLY_RECIPE_ACTIONS.forEach((key, recipeAction)->{
			try {
				if(recipeAction.register()) {
					LOGGER.debug("Registered early recipe with key {}", key);
					recipeCount.incrementAndGet();
				}
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Early recipe with ID {} received invalid arguments: {}", key, e.getMessage());
				return;
			}
			catch(Throwable e) {
				LOGGER.error("Early recipe with ID {} errored", key, e);
				return;
			}
		});
		EXECUTED_RECIPE_ACTIONS.addAll(EARLY_RECIPE_ACTIONS.keySet());
		EARLY_RECIPE_ACTIONS.clear();
		LOGGER.info("Registered {} early recipes", recipeCount.get());
	}

	public static void registerRecipes() {
		AtomicInteger recipeCount = new AtomicInteger(0);
		RECIPE_ACTIONS.forEach((key, recipeAction)->{
			try {
				if(recipeAction.register()) {
					LOGGER.debug("Registered recipe with key {}", key);
					recipeCount.incrementAndGet();
				}
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Recipe with ID {} received invalid arguments: {}", key, e.getMessage());
				return;
			}
			catch(Throwable e) {
				LOGGER.error("Recipe with ID {} errored", key, e);
				return;
			}
		});
		EXECUTED_RECIPE_ACTIONS.addAll(RECIPE_ACTIONS.keySet());
		RECIPE_ACTIONS.clear();
		LOGGER.info("Registered {} recipes", recipeCount.get());
	}

	public static void registerLateRecipes() {
		AtomicInteger recipeCount = new AtomicInteger(0);
		LATE_RECIPE_ACTIONS.forEach((key, recipeAction)->{
			try {
				if(recipeAction.register()) {
					LOGGER.debug("Registered late recipe with key {}", key);
					recipeCount.incrementAndGet();
				}
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Late recipe with ID {} received invalid arguments: {}", key, e.getMessage());
				return;
			}
			catch(Throwable e) {
				LOGGER.error("Late recipe with ID {} errored", key, e);
				return;
			}
		});
		EXECUTED_RECIPE_ACTIONS.addAll(LATE_RECIPE_ACTIONS.keySet());
		LATE_RECIPE_ACTIONS.clear();
		LOGGER.info("Registered {} late recipes", recipeCount.get());
	}
}
