package thelm.jaopca.api.blocks;

import java.util.function.Supplier;

public interface IBlockItemCreator {

	IMaterialFormBlockItem create(IMaterialFormBlock block, Supplier<IBlockFormSettings> settings);
}
