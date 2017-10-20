package thelm.jaopca.api.item;

import java.util.Arrays;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public abstract class ItemMetaBase extends ItemBase {

	public String[] prefixes = new String[getMaxMeta()];
	
	public ItemMetaBase(ItemEntry itemEntry, IOreEntry oreEntry) {
		super(itemEntry, oreEntry);
		setHasSubtypes(true);
		Arrays.fill(prefixes, itemEntry.prefix);
	}
	
	public String[] getPrefixes() {
		return prefixes;
	}

	@Override
	public String getPrefix(int meta) {
		return getPrefixes()[meta];
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			for(int i = 0; i <= getMaxMeta(); ++i) {
				if(this.hasMeta(i)) {
					items.add(new ItemStack(this, 1, i));
				}
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack)+stack.getItemDamage();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		for(int i = 0; i <= getMaxMeta(); ++i) {
			if(this.hasMeta(i)) {
				ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getItemEntry().itemModelLocation.toString().split("#")[0]+"#"+i));
			}
		}
	}
}
