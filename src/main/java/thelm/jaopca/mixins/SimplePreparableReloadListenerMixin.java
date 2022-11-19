package thelm.jaopca.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import thelm.jaopca.events.CommonEventHandler;

@Mixin(SimplePreparableReloadListener.class)
public abstract class SimplePreparableReloadListenerMixin<T> {

	@Inject(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimplePreparableReloadListener;apply(Ljava/lang/Object;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"))
	public void beforeApply(ResourceManager resourceManager, ProfilerFiller profiler, Object object, CallbackInfo info) {
		CommonEventHandler.INSTANCE.onReloadApply(getClass(), object);
	}
}
