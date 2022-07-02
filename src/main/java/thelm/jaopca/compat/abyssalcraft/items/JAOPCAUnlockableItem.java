package thelm.jaopca.compat.abyssalcraft.items;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.item.ICrystal;
import com.shinoow.abyssalcraft.api.item.IUnlockableItem;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.items.JAOPCAItem;

public class JAOPCAUnlockableItem extends JAOPCAItem implements IUnlockableItem {

	private IUnlockCondition unlockCondition;

	public JAOPCAUnlockableItem(IForm form, IMaterial material, IItemFormSettings settings) {
		super(form, material, settings);
	}

	@Override
	public JAOPCAUnlockableItem setUnlockCondition(IUnlockCondition unlockCondition) {
		this.unlockCondition = unlockCondition;
		return this;
	}

	@Override
	public IUnlockCondition getUnlockCondition(ItemStack stack) {
		return unlockCondition;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public FontRenderer getFontRenderer(ItemStack stack) {
		return APIUtils.getFontRenderer(stack);
	}
}
