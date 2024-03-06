package thelm.jaopca.api.blocks;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlock extends IMaterialForm {

	default Block toBlock() {
		return (Block)this;
	}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}
}
