package thelm.jaopca.api.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materialforms.IMaterialForm;

public abstract class MaterialFormItem extends Item implements IMaterialForm {

	public MaterialFormItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return JAOPCAApi.instance().currentLocalizer().localizeMaterialForm(getForm(), getMaterial(), getTranslationKey());
	}
}
