package thelm.jaopca.recipes;

import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedSet;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import thelm.jaopca.api.recipes.IRecipeAction;

public class RecipeHandler {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<String, IRecipeAction> EARLY_RECIPE_ACTIONS = new TreeMap<>();
	private static final TreeMap<String, IRecipeAction> RECIPE_ACTIONS = new TreeMap<>();
	private static final TreeMap<String, IRecipeAction> LATE_RECIPE_ACTIONS = new TreeMap<>();
	private static final TreeMap<String, IRecipeAction> FINAL_RECIPE_ACTIONS = new TreeMap<>();
	private static final TreeSet<String> EXECUTED_RECIPE_ACTIONS = new TreeSet<>();

	public static boolean registerEarlyRecipe(String key, IRecipeAction recipeAction) {
		if(Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
			return false;
		}
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeAction);
		return EARLY_RECIPE_ACTIONS.putIfAbsent(key, recipeAction) == null;
	}

	public static boolean registerRecipe(String key, IRecipeAction recipeAction) {
		if(Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
			return false;
		}
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeAction);
		return RECIPE_ACTIONS.putIfAbsent(key, recipeAction) == null;
	}

	public static boolean registerLateRecipe(String key, IRecipeAction recipeAction) {
		if(Loader.instance().hasReachedState(LoaderState.AVAILABLE)) {
			return false;
		}
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeAction);
		return LATE_RECIPE_ACTIONS.putIfAbsent(key, recipeAction) == null;
	}

	public static boolean registerFinalRecipe(String key, IRecipeAction recipeAction) {
		if(Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
			return false;
		}
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeAction);
		return LATE_RECIPE_ACTIONS.putIfAbsent(key, recipeAction) == null;
	}

	public static Set<String> getRegisteredRecipes() {
		return ImmutableSortedSet.<String>naturalOrder().
				addAll(EARLY_RECIPE_ACTIONS.keySet()).
				addAll(RECIPE_ACTIONS.keySet()).
				addAll(LATE_RECIPE_ACTIONS.keySet()).
				addAll(FINAL_RECIPE_ACTIONS.keySet()).
				addAll(EXECUTED_RECIPE_ACTIONS).build();
	}

	public static void registerEarlyRecipes() {
		AtomicInteger recipeCount = new AtomicInteger(0);
		EARLY_RECIPE_ACTIONS.forEach((key, recipeAction)->{
			try {
				if(recipeAction.register()) {
					LOGGER.debug("Registered early recipe with key {}", new Object[] {key});
					recipeCount.incrementAndGet();
				}
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Early recipe with ID {} received invalid arguments: {}", new Object[] {key, e.getMessage()});
				return;
			}
			catch(Throwable e) {
				LOGGER.error("Early recipe with ID {} errored", new Object[] {key, e});
				return;
			}
		});
		EXECUTED_RECIPE_ACTIONS.addAll(EARLY_RECIPE_ACTIONS.keySet());
		EARLY_RECIPE_ACTIONS.clear();
		LOGGER.info("Registered {} early recipes", new Object[] {recipeCount.get()});
	}

	public static void registerRecipes() {
		AtomicInteger recipeCount = new AtomicInteger(0);
		RECIPE_ACTIONS.forEach((key, recipeAction)->{
			try {
				if(recipeAction.register()) {
					LOGGER.debug("Registered recipe with key {}", new Object[] {key});
					recipeCount.incrementAndGet();
				}
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Recipe with ID {} received invalid arguments: {}", new Object[] {key, e.getMessage()});
				return;
			}
			catch(Throwable e) {
				LOGGER.error("Recipe with ID {} errored", new Object[] {key, e});
				return;
			}
		});
		EXECUTED_RECIPE_ACTIONS.addAll(RECIPE_ACTIONS.keySet());
		RECIPE_ACTIONS.clear();
		LOGGER.info("Registered {} recipes", new Object[] {recipeCount.get()});
	}

	public static void registerLateRecipes() {
		AtomicInteger recipeCount = new AtomicInteger(0);
		LATE_RECIPE_ACTIONS.forEach((key, recipeAction)->{
			try {
				if(recipeAction.register()) {
					LOGGER.debug("Registered late recipe with key {}", new Object[] {key});
					recipeCount.incrementAndGet();
				}
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Late recipe with ID {} received invalid arguments: {}", new Object[] {key, e.getMessage()});
				return;
			}
			catch(Throwable e) {
				LOGGER.error("Late recipe with ID {} errored", new Object[] {key, e});
				return;
			}
		});
		EXECUTED_RECIPE_ACTIONS.addAll(LATE_RECIPE_ACTIONS.keySet());
		LATE_RECIPE_ACTIONS.clear();
		LOGGER.info("Registered {} late recipes", new Object[] {recipeCount.get()});
	}

	public static void registerFinalRecipes() {
		AtomicInteger recipeCount = new AtomicInteger(0);
		FINAL_RECIPE_ACTIONS.forEach((key, recipeAction)->{
			try {
				if(recipeAction.register()) {
					LOGGER.debug("Registered final recipe with key {}", new Object[] {key});
					recipeCount.incrementAndGet();
				}
			}
			catch(IllegalArgumentException e) {
				LOGGER.warn("Final recipe with ID {} received invalid arguments: {}", new Object[] {key, e.getMessage()});
				return;
			}
			catch(Throwable e) {
				LOGGER.error("Final recipe with ID {} errored", new Object[] {key, e});
				return;
			}
		});
		EXECUTED_RECIPE_ACTIONS.addAll(FINAL_RECIPE_ACTIONS.keySet());
		FINAL_RECIPE_ACTIONS.clear();
		LOGGER.info("Registered {} final recipes", new Object[] {recipeCount.get()});
	}
}
