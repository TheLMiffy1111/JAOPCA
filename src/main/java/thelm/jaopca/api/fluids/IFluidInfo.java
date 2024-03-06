package thelm.jaopca.api.fluids;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import thelm.jaopca.api.blocks.IBlockLike;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IFluidInfo extends IMaterialFormInfo, IFluidLike, IBlockLike, ItemLike {

	IMaterialFormFluid getMaterialFormFluid();

	IMaterialFormFluidType getMaterialFormFluidType();

	IMaterialFormFluidBlock getMaterialFormFluidBlock();

	IMaterialFormBucketItem getMaterialFormBucketItem();

	default Fluid getFluid() {
		return getMaterialFormFluid().toFluid();
	}

	default FluidType getFluidType() {
		return getMaterialFormFluidType().toFluidType();
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
