package thelm.jaopca.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.renderer.texture.SpriteContents;

@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {

	@Invoker("getFrameCount")
	int frameCount();
}
