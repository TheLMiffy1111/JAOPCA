package thelm.jaopca.api.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.block.IBlockWithProperty;
import thelm.jaopca.api.utils.Utils;

public class ItemBlockBase extends ItemBlock implements IItemBlockWithProperty {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public EnumRarity rarity;

	public ItemBlockBase(IBlockWithProperty block) {
		super((Block)block);
		setRegistryName(((Block)block).getRegistryName());
		this.oreEntry = block.getOreEntry();
		this.itemEntry = block.getItemEntry();
	}

	@Override
	public IOreEntry getOreEntry() {
		return oreEntry;
	}

	@Override
	public ItemEntry getItemEntry() {
		return itemEntry;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity == EnumRarity.COMMON ? super.getRarity(stack) : rarity;
	}

	@Override
	public ItemBlockBase setRarity(EnumRarity rarity) {
		this.rarity = rarity;
		return this;
	}

	@Override
	public ItemBlockBase setMaxStackSize(int maxStackSize) {
		super.setMaxStackSize(maxStackSize);
		return this;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if(I18n.canTranslate(getUnlocalizedNameInefficiently(stack)+'.'+oreEntry.getOreName())) {
			return I18n.translateToLocal(getUnlocalizedNameInefficiently(stack)+'.'+oreEntry.getOreName());
		}
		return String.format(super.getItemStackDisplayName(stack), I18n.canTranslate("jaopca.entry."+oreEntry.getOreName()) ? I18n.translateToLocal("jaopca.entry."+oreEntry.getOreName()) : Utils.toSpaceSeparated(oreEntry.getOreName()));
	}
}
