package thelm.jaopca.api.fluids;

import net.minecraft.block.Block;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluidBlock extends IMaterialForm {

	default Block asBlock() {
		return (Block)this;
	}
}
