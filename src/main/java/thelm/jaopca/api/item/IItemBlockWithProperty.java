package thelm.jaopca.api.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public interface IItemBlockWithProperty extends IObjectWithProperty {
	
	IItemBlockWithProperty setMaxStackSize(int maxStkSize);
	IItemBlockWithProperty setRarity(EnumRarity rarity);

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {
		ModelLoader.setCustomModelResourceLocation((Item)this, 0, (ModelResourceLocation)getItemEntry().itemModelLocation);
	}
}
