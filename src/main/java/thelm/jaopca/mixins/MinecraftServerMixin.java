package thelm.jaopca.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import thelm.jaopca.events.CommonEventHandler;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(method = "configurePackRepository", at = @At(value = "INVOKE",
			target = "net/minecraft/server/packs/repository/PackRepository.reload()V",
			shift = At.Shift.BEFORE))
	private static void onConfigurePackRepository(PackRepository packRepository, DataPackConfig dataPackConfig, boolean forceVanilla, CallbackInfoReturnable<DataPackConfig> info) {
		CommonEventHandler.getInstance().onDataPackDiscovery(packRepository);
	}
}
