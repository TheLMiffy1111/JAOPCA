package thelm.jaopca.api.fluids;

import net.minecraft.world.level.block.Block;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluidBlock extends IMaterialForm {

	default Block toBlock() {
		return (Block)this;
	}
}
