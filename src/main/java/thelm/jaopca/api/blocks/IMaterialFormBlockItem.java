package thelm.jaopca.api.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlockItem extends IMaterialForm {

	default BlockItem toBlockItem() {
		return (BlockItem)this;
	}

	default void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		output.accept(toBlockItem());
	}
}
