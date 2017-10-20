package thelm.jaopca.api.item;

import java.util.function.Predicate;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.IOreEntry;

public interface IItemWithProperty extends IObjectWithProperty {

	IItemWithProperty setMaxStackSize(int maxStkSize);
	IItemWithProperty setFull3D(boolean full3D);
	IItemWithProperty setRarity(EnumRarity rarity);
	IItemWithProperty setHasEffect(boolean hasEffect);

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {
		ModelLoader.setCustomModelResourceLocation((Item)this, 0, getItemEntry().itemModelLocation);
	}
}
