package thelm.jaopca.api.blocks;

import net.minecraft.world.level.block.Block;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlock extends IMaterialForm {

	default Block asBlock() {
		return (Block)this;
	}
}
