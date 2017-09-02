package thelm.jaopca.api.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.utils.Utils;

public class ItemBase extends Item implements IItemWithProperty {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public EnumRarity rarity = EnumRarity.COMMON;

	public ItemBase(ItemEntry itemEntry, IOreEntry oreEntry) {
		setUnlocalizedName("jaopca."+itemEntry.name);
		setRegistryName("jaopca:item_"+itemEntry.name+oreEntry.getOreName());
		this.oreEntry = oreEntry;
		this.itemEntry = itemEntry;
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
	public ItemBase setRarity(EnumRarity rarity) {
		this.rarity = rarity;
		return this;
	}

	@Override
	public ItemBase setFull3D(boolean value) {
		bFull3D = value;
		return this;
	}

	@Override
	public ItemBase setMaxStackSize(int maxStackSize) {
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
