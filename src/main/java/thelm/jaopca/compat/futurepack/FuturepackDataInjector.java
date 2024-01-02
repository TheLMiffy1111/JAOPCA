package thelm.jaopca.compat.futurepack;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import futurepack.common.recipes.RecipeManagerSyncer;
import futurepack.common.recipes.centrifuge.FPZentrifugeManager;
import futurepack.common.recipes.centrifuge.ZentrifugeRecipe;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import thelm.jaopca.config.ConfigHandler;

public class FuturepackDataInjector extends ReloadListener<Void> {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<ResourceLocation, Supplier<ZentrifugeRecipe>> ZENTRIFUGE_RECIPES_INJECT = new TreeMap<>();
	private static MinecraftServer server;

	public static void onConstruct() {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, FuturepackDataInjector::onAddReloadListener);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, FuturepackDataInjector::onServerAboutToStart);
	}

	public static void onAddReloadListener(AddReloadListenerEvent event) {
		event.addListener(new FuturepackDataInjector());
	}

	public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {
		server = event.getServer();
		injectRecipes();
	}

	public static boolean registerZentrifugeRecipe(ResourceLocation key, Supplier<ZentrifugeRecipe> recipeSupplier) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(recipeSupplier);
		if(ConfigHandler.RECIPE_BLACKLIST.contains(key) ||
				ConfigHandler.RECIPE_REGEX_BLACKLIST.stream().anyMatch(p->p.matcher(key.toString()).matches())) {
			return false;
		}
		return ZENTRIFUGE_RECIPES_INJECT.putIfAbsent(key, recipeSupplier) != null;
	}

	private FuturepackDataInjector() {

	}

	@Override
	protected Void prepare(IResourceManager resourceManager, IProfiler profiler) {
		return null;
	}

	@Override
	protected void apply(Void splashList, IResourceManager resourceManager, IProfiler profiler) {
		injectRecipes();
	}

	private static void injectRecipes() {
		if(server != null) {
			ZENTRIFUGE_RECIPES_INJECT.forEach((key, supplier)->{
				ZentrifugeRecipe recipe = null;
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
				FPZentrifugeManager.instance.addRecipe(recipe);
				LOGGER.debug("Injected recipe with ID {}", key);
			});
			RecipeManagerSyncer.INSTANCE.onRecipeReload(server);
		}
	}
}
