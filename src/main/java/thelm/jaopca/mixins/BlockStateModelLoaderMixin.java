package thelm.jaopca.mixins;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.client.models.ModelHandler;

@Mixin(BlockStateModelLoader.class)
public class BlockStateModelLoaderMixin {

	@Shadow
	private Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources;

	@Inject(method = "<init>", at = @At(value = "FIELD", shift = At.Shift.AFTER, target = "blockStateResources", opcode = Opcodes.PUTFIELD))
	public void onInitBlockStateResources(CallbackInfo ci) {
		ModelHandler.gatherBlockStateRemaps(Collections.unmodifiableSet(blockStateResources.keySet()));
	}

	@ModifyVariable(method = "loadBlockStateDefinitions", at = @At("HEAD"), argsOnly = true)
	public ResourceLocation onLoadBlockStateDefinitions(ResourceLocation location) {
		return ModelHandler.remapBlockModel(location);
	}
}
