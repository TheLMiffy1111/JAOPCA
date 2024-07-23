package thelm.jaopca.mixins;

import java.util.Collections;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.client.models.ModelHandler;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {

	@Shadow
	private Map<ResourceLocation, BlockModel> modelResources;

	@Inject(method = "<init>", at = @At(value = "FIELD", shift = At.Shift.AFTER, target = "modelResources", opcode = Opcodes.PUTFIELD))
	public void onInitModelResources(CallbackInfo ci) {
		ModelHandler.gatherItemModelRemaps(Collections.unmodifiableSet(modelResources.keySet()));
	}

	@ModifyVariable(method = "loadItemModelAndDependencies", at = @At("HEAD"), argsOnly = true)
	public ResourceLocation onLoadItemModelAndDependencies(ResourceLocation location) {
		return ModelHandler.remapItemModel(location);
	}
}
