package thelm.jaopca.api.item;

import net.minecraft.item.EnumRarity;

public interface IItemWithProperty {
	
	IItemWithProperty setMaxStackSize(int maxStkSize);
	IItemWithProperty setFull3D(boolean full3D);
	IItemWithProperty setRarity(EnumRarity rarity);
}
