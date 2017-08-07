package thelm.jaopca.api.item;

import net.minecraft.item.EnumRarity;

public interface IItemBlockWithProperty {

	IItemBlockWithProperty setMaxStackSize(int maxStkSize);
	IItemBlockWithProperty setRarity(EnumRarity rarity);
}
