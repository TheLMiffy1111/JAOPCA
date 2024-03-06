package thelm.jaopca.mixins;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.util.DeferredSoundType;

@Mixin(DeferredSoundType.class)
public interface DeferredSoundTypeAccessor {

	@Accessor("breakSound")
	Supplier<SoundEvent> breakSound();

	@Accessor("stepSound")
	Supplier<SoundEvent> stepSound();

	@Accessor("placeSound")
	Supplier<SoundEvent> placeSound();

	@Accessor("hitSound")
	Supplier<SoundEvent> hitSound();

	@Accessor("fallSound")
	Supplier<SoundEvent> fallSound();
}
