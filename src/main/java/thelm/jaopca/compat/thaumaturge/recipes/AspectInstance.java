package thelm.jaopca.compat.thaumaturge.recipes;

import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record AspectInstance(ResourceLocation aspect, int amount) {

	public static final Codec<AspectInstance> CODEC = RecordCodecBuilder.create(instance->instance.group(
			ResourceLocation.CODEC.fieldOf("aspect").forGetter(AspectInstance::aspect),
			Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1).forGetter(AspectInstance::amount)).
			apply(instance, AspectInstance::new));

	public static AspectInstance of(ResourceLocation aspect, int amount) {
		return new AspectInstance(aspect, amount);
	}

	public static AspectInstance of(ResourceKey<IAspect> aspect, int amount) {
		return new AspectInstance(aspect.location(), amount);
	}
}
