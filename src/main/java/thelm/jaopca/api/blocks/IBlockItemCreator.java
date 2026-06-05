package thelm.jaopca.api.blocks;

import net.minecraft.resources.Identifier;

public interface IBlockItemCreator {

	IMaterialFormBlockItem create(IMaterialFormBlock block, IBlockFormSettings settings, Identifier registryName);
}
