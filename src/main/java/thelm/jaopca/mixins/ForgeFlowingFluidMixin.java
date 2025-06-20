package thelm.jaopca.mixins;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;

// This is to fix an issue with Assembly Line Machines so that I can actually debug
@Mixin(ForgeFlowingFluid.class)
public class ForgeFlowingFluidMixin {

	@Shadow
	private Supplier<? extends Fluid> still;
	@Shadow
	private Supplier<? extends Fluid> flowing;

	@Overwrite
	public boolean isSame(Fluid fluid) {
		return still != null && fluid == still.get() || flowing != null && fluid == flowing.get();
	}
}
