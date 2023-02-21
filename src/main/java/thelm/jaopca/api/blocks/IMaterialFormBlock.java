package thelm.jaopca.api.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlock extends IMaterialForm {

	default Block asBlock() {
		return (Block)this;
	}

	@SideOnly(Side.CLIENT)
	default boolean hasOverlay() {
		return false;
	}
}
