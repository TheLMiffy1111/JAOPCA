package thelm.jaopca.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import thelm.jaopca.events.CommonEventHandler;

@Mixin(value = RecipeManager.class, priority = 900)
public class RecipeManagerMixin {

	@Inject(method = "apply", at = @At("HEAD"))
	private void onApply(Map<ResourceLocation, JsonElement> recipeMap, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo info) {
		CommonEventHandler.getInstance().onReadRecipes(recipeMap);
	}
}
