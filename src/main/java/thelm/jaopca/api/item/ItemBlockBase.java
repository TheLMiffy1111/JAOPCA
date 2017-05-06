package thelm.jaopca.api.item;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.block.BlockBase;

public class ItemBlockBase extends ItemBlock {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public ItemBlockBase(BlockBase block) {
		super(block);
		setRegistryName(block.getRegistryName());
		this.oreEntry = block.oreEntry;
		this.itemEntry = block.itemEntry;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return String.format(super.getItemStackDisplayName(stack), I18n.canTranslate("jaopca.entry."+oreEntry.getOreName()) ? I18n.translateToLocal("jaopca.entry."+oreEntry.getOreName()) : oreEntry.getOreName());
	}
}
