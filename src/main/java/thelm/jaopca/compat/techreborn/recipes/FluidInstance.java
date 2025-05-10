package thelm.jaopca.compat.techreborn.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import reborncore.common.fluid.FluidValue;

public record FluidInstance(FluidVariant fluidVariant, FluidValue amount) {

	public static final Codec<FluidInstance> CODEC = RecordCodecBuilder.create(instance->instance.group(
			FluidVariant.CODEC.fieldOf("fluid").forGetter(FluidInstance::fluidVariant),
			FluidValue.CODEC.fieldOf("amount").forGetter(FluidInstance::amount)).
			apply(instance, FluidInstance::new));
}
