package thelm.jaopca.api.fluids;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import thelm.jaopca.api.blocks.IBlockProvider;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IFluidInfo extends IMaterialFormInfo, IFluidProvider, IBlockProvider, IItemProvider {

	IMaterialFormFluid getMaterialFormFluid();

	IMaterialFormFluidBlock getMaterialFormFluidBlock();

	IMaterialFormBucketItem getMaterialFormBucketItem();

	default Fluid getFluid() {
		return getMaterialFormFluid().toFluid();
	}

	default Block getFluidBlock() {
		return getMaterialFormFluidBlock().toBlock();
	}

	default Item getBucketItem() {
		return getMaterialFormBucketItem().toItem();
	}

	@Override
	default IMaterialFormFluid getMaterialForm() {
		return getMaterialFormFluid();
	}

	@Override
	default Fluid asFluid() {
		return getFluid();
	}

	@Override
	default Block asBlock() {
		return getFluidBlock();
	}

	@Override
	default Item asItem() {
		return getBucketItem();
	}
}
