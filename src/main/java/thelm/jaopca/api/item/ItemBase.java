package thelm.jaopca.api.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public class ItemBase extends Item {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public ItemBase(ItemEntry itemEntry, IOreEntry oreEntry) {
		setUnlocalizedName("jaopca."+itemEntry.name);
		setRegistryName("jaopca:item_"+itemEntry.name+oreEntry.getOreName());
		this.oreEntry = oreEntry;
		this.itemEntry = itemEntry;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return String.format(super.getItemStackDisplayName(stack), I18n.canTranslate("jaopca.entry."+oreEntry.getOreName()) ? I18n.translateToLocal("jaopca.entry."+oreEntry.getOreName()) : oreEntry.getOreName());
	}
}
