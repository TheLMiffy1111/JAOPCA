package thelm.jaopca.api.item;

import net.minecraft.item.EnumRarity;
import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public interface IItemWithProperty extends IObjectWithProperty {
	
	IItemWithProperty setMaxStackSize(int maxStkSize);
	IItemWithProperty setFull3D(boolean full3D);
	IItemWithProperty setRarity(EnumRarity rarity);
}
