package thelm.jaopca.api.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IObjectWithProperty;

public interface IItemWithProperty extends IObjectWithProperty {
	
	IItemWithProperty setMaxStackSize(int maxStkSize);
	IItemWithProperty setFull3D(boolean full3D);
	IItemWithProperty setRarity(EnumRarity rarity);

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {
		ModelLoader.setCustomModelResourceLocation((Item)this, 0, (ModelResourceLocation)getItemEntry().itemModelLocation);
	}
}
