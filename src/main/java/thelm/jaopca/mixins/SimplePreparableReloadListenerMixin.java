package thelm.jaopca.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import thelm.jaopca.events.CommonEventHandler;

@Mixin(SimplePreparableReloadListener.class)
public abstract class SimplePreparableReloadListenerMixin<T> {

	@ModifyArg(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimplePreparableReloadListener;apply(Ljava/lang/Object;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"))
	public Object beforeApply(Object object) {
		CommonEventHandler.INSTANCE.onReloadApply(getClass(), object);
		return object;
	}
}
