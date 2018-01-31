package thelm.jaopca.api.item;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IObjectWithProperty;

public interface IItemWithProperty extends IObjectWithProperty {

	default IItemWithProperty setMaxStackSize(int maxStkSize) {
		return this;
	}

	default IItemWithProperty setFull3D(boolean full3D) {
		return this;
	}

	default IItemWithProperty setRarity(EnumRarity rarity) {
		return this;
	}

	default IItemWithProperty setHasEffect(boolean hasEffect) {
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	default void registerModels() {
		ModelLoader.setCustomModelResourceLocation((Item)this, 0, getItemEntry().itemModelLocation);
	}
}
