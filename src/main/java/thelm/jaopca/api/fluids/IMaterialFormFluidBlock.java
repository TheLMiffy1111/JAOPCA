package thelm.jaopca.api.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.state.IntegerProperty;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluidBlock extends IMaterialForm {

	default Block asBlock() {
		return (Block)this;
	}
}
