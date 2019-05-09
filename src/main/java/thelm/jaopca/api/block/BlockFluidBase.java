package thelm.jaopca.api.block;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.fluid.IFluidWithProperty;

public class BlockFluidBase extends BlockFluidClassic implements IBlockFluidWithProperty {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public BlockFluidBase(IFluidWithProperty fluid, Material material) {
		super((Fluid)fluid, material);
		oreEntry = fluid.getOreEntry();
		itemEntry = fluid.getItemEntry();
		setRegistryName("jaopca:fluid_"+itemEntry.name+oreEntry.getOreName());
		setTranslationKey("jaopca."+itemEntry.name);
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
	public BlockFluidBase setQuantaPerBlock(int quantaPerBlock) {
		super.setQuantaPerBlock(quantaPerBlock);
		return this;
	}
}
