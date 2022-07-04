package thelm.jaopca.api.blocks;

import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IBlockModelMapCreator {

	Map<IBlockState, ModelResourceLocation> create(IMaterialFormBlock block, IBlockFormSettings settings);
}
