package thelm.jaopca.api.blocks;

import java.util.function.Supplier;

public interface IBlockItemCreator {

	MaterialFormBlockItem create(BlockMaterialForm block, Supplier<IBlockFormSettings> settings);
}
