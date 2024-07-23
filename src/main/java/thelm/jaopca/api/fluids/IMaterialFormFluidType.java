package thelm.jaopca.api.fluids;

import java.util.function.Consumer;

import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluidType extends IMaterialForm {

	default FluidType toFluidType() {
		return (FluidType)this;
	}

	default void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {}
}
