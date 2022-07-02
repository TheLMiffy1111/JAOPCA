package thelm.jaopca.api.fluids;

import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IFluidBlockModelMapCreator {

	Map<IBlockState, ModelResourceLocation> create(IMaterialFormFluidBlock fluidBlock, IFluidFormSettings settings);
}
