package thelm.jaopca.api.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.block.IBlockWithProperty;

public interface IItemBlockWithProperty extends IObjectWithProperty {

	default IItemBlockWithProperty setMaxStackSize(int maxStkSize) {
		return this;
	}

	default IItemBlockWithProperty setRarity(EnumRarity rarity) {
		return this;
	}

	@Override
	default int getMaxMeta() {
		IBlockWithProperty block = (IBlockWithProperty)((ItemBlock)this).getBlock();
		return block.hasSubtypes() ? block.getMaxMeta() : IObjectWithProperty.super.getMaxMeta();
	}

	@Override
	default boolean hasMeta(int meta) {
		IBlockWithProperty block = (IBlockWithProperty)((ItemBlock)this).getBlock();
		return block.hasSubtypes() ? block.hasMeta(meta) : IObjectWithProperty.super.hasMeta(meta);
	}

	@Override
	default String getPrefix(int meta) {
		IBlockWithProperty block = (IBlockWithProperty)((ItemBlock)this).getBlock();
		return block.hasSubtypes() ? block.getPrefix(meta) : IObjectWithProperty.super.getPrefix(meta);
	}

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {
		IBlockWithProperty block = (IBlockWithProperty)((ItemBlock)this).getBlock();
		if(block.hasSubtypes()) {
			for(int i = 0; i <= getMaxMeta(); ++i) {
				if(hasMeta(i)) {
					ModelLoader.setCustomModelResourceLocation((Item)this, i, new ModelResourceLocation(getItemEntry().itemModelLocation.toString().split("#")[0]+"#"+i));
				}
			}
		}
		else {
			ModelLoader.setCustomModelResourceLocation((Item)this, 0, getItemEntry().itemModelLocation);
		}
	}
}
