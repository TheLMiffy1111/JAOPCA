package thelm.jaopca.api.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormItem extends IMaterialForm {

	default Item asItem() {
		return (Item)this;
	}

	@SideOnly(Side.CLIENT)
	default IItemRenderer getRenderer() {
		return null;
	}
}
