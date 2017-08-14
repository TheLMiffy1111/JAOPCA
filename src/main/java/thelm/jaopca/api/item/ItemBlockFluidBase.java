package thelm.jaopca.api.item;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.block.IBlockFluidWithProperty;

public class ItemBlockFluidBase extends ItemBlock implements IItemBlockFluidWithProperty {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;
	public final Fluid fluid;

	public EnumRarity rarity;

	public ItemBlockFluidBase(IBlockFluidWithProperty block) {
		super((Block)block);
		setRegistryName(((Block)block).getRegistryName());
		oreEntry = block.getOreEntry();
		itemEntry = block.getItemEntry();
		fluid = ((IFluidBlock)block).getFluid();
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
	public String getItemStackDisplayName(ItemStack stack) {
		return fluid.getLocalizedName(new FluidStack(fluid, 0));
	}
}
