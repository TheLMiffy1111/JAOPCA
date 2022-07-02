package thelm.jaopca.compat.abyssalcraft.blocks;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.item.ICrystal;
import com.shinoow.abyssalcraft.api.item.IUnlockableItem;
import com.shinoow.abyssalcraft.api.necronomicon.condition.IUnlockCondition;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.blocks.JAOPCABlockItem;

public class JAOPCAUnlockableBlockItem extends JAOPCABlockItem implements IUnlockableItem {

	private IUnlockCondition unlockCondition;

	public JAOPCAUnlockableBlockItem(IMaterialFormBlock block, IBlockFormSettings settings) {
		super(block, settings);
	}

	@Override
	public JAOPCAUnlockableBlockItem setUnlockCondition(IUnlockCondition unlockCondition) {
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
