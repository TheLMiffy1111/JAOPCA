package thelm.jaopca.api.blocks;

import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public interface IBlockItemModelFunctionCreator {

	Pair<Function<ItemStack, ModelResourceLocation>, Set<ModelResourceLocation>> create(IMaterialFormBlockItem blockItem, IBlockFormSettings settings);
}
