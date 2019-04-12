package thelm.jaopca.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materialforms.IMaterialForm;

public abstract class BlockMaterialForm extends Block implements IMaterialForm {

	public BlockMaterialForm(Block.Properties properties) {
		super(properties);
	}

	@Override
	public ITextComponent getNameTextComponent() {
		return JAOPCAApi.instance().currentLocalizer().localizeMaterialForm(getForm(), getMaterial(), getTranslationKey());
	}
}
