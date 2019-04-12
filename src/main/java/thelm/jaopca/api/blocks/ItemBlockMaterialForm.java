package thelm.jaopca.api.blocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;

public abstract class ItemBlockMaterialForm extends ItemBlock implements IMaterialForm {

	public ItemBlockMaterialForm(BlockMaterialForm block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public IForm getForm() {
		return ((IMaterialForm)getBlock()).getForm();
	}

	@Override
	public IMaterial getMaterial() {
		return ((IMaterialForm)getBlock()).getMaterial();
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return JAOPCAApi.instance().currentLocalizer().localizeMaterialForm(getForm(), getMaterial(), getTranslationKey());
	}
}
