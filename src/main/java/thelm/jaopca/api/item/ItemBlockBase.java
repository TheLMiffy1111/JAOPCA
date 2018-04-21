package thelm.jaopca.api.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.block.IBlockWithProperty;
import thelm.jaopca.api.utils.Utils;

public class ItemBlockBase extends ItemBlock implements IItemBlockWithProperty {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public EnumRarity rarity;

	public ItemBlockBase(IBlockWithProperty block) {
		super((Block)block);
		setRegistryName(((Block)block).getRegistryName());
		this.oreEntry = block.getOreEntry();
		this.itemEntry = block.getItemEntry();
	}

	@Override
	public IOreEntry getOreEntry() {
		return oreEntry;
	}

	@Override
	public ItemEntry getItemEntry() {
		return itemEntry;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity == EnumRarity.COMMON ? super.getRarity(stack) : rarity;
	}

	@Override
	public ItemBlockBase setRarity(EnumRarity rarity) {
		this.rarity = rarity;
		return this;
	}

	@Override
	public ItemBlockBase setMaxStackSize(int maxStackSize) {
		super.setMaxStackSize(maxStackSize);
		return this;
	}

	@Override
	public boolean getHasSubtypes() {
		return ((IBlockWithProperty)this.getBlock()).hasSubtypes();
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
	public String getItemStackDisplayName(ItemStack stack) {
		return Utils.smartLocalize(this.getUnlocalizedName()+".name", this.getUnlocalizedName()+".%s.name", this.getOreEntry());
	}
}
