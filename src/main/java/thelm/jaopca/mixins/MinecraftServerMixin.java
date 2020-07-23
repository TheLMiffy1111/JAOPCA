package thelm.jaopca.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.codec.DatapackCodec;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.events.CommonEventHandler;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "func_240772_a_", at = @At(value = "INVOKE",
			target = "net/minecraft/resources/ResourcePackList.reloadPacksFromFinders()V",
			shift = At.Shift.BEFORE))
	private static void onReloadDatapacks(ResourcePackList<ResourcePackInfo> resourcePacks, DatapackCodec codec, boolean forceVanilla, CallbackInfoReturnable<DatapackCodec> info) {
		CommonEventHandler.getInstance().onDataPackDiscovery(resourcePacks);
	}
}
