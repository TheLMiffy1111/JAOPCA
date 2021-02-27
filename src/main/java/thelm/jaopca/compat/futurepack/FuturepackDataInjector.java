package thelm.jaopca.compat.futurepack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

public class FuturepackDataInjector extends ReloadListener {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final List<Supplier<ZentrifugeRecipe>> ZENTRIFUGE_RECIPES_INJECT = new ArrayList<>();
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

	public static boolean registerZentrifugeRecipe(Supplier<ZentrifugeRecipe> recipeSupplier) {
		Objects.requireNonNull(recipeSupplier);
		return ZENTRIFUGE_RECIPES_INJECT.add(recipeSupplier);
	}


	private FuturepackDataInjector() {

	}

	@Override
	protected Object prepare(IResourceManager resourceManager, IProfiler profiler) {
		return null;
	}

	@Override
	protected void apply(Object splashList, IResourceManager resourceManager, IProfiler profiler) {
		injectRecipes();
	}

	private static void injectRecipes() {
		if(server != null) {
			for(Supplier<ZentrifugeRecipe> supplier : ZENTRIFUGE_RECIPES_INJECT) {
				ZentrifugeRecipe recipe = null;
				try {
					recipe = supplier.get();
				}
				catch(IllegalArgumentException e) {
					LOGGER.warn("Zentrifuge recipe received invalid arguments: {}", e.getMessage());
					continue;
				}
				if(recipe == null) {
					LOGGER.warn("A zentrifuge recipe returned null");
				}
				else {
					FPZentrifugeManager.instance.addRecipe(recipe);
				}
			}
			RecipeManagerSyncer.INSTANCE.onRecipeReload(server);
		}
	}
}
