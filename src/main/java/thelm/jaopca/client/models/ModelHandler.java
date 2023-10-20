package thelm.jaopca.client.models;

import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.items.ItemFormType;

public class ModelHandler {

	public static void registerModels() {
		for(IMaterialFormBlock materialFormBlock : BlockFormType.getBlocks()) {
			Block block = materialFormBlock.asBlock();
			IBlockFormSettings settings = (IBlockFormSettings)materialFormBlock.getForm().getSettings();
			ModelLoader.setCustomStateMapper(block, b->settings.getBlockModelMapCreator().create(materialFormBlock, settings));
		}
		for(IMaterialFormBlockItem materialFormBlockItem : BlockFormType.getBlockItems()) {
			ItemBlock blockItem = materialFormBlockItem.asBlockItem();
			IBlockFormSettings settings = (IBlockFormSettings)materialFormBlockItem.getForm().getSettings();
			Pair<Function<ItemStack, ModelResourceLocation>, Set<ModelResourceLocation>> funcPair =
					settings.getBlockItemModelFunctionCreator().create(materialFormBlockItem, settings);
			ModelLoader.setCustomMeshDefinition(blockItem, s->funcPair.getLeft().apply(s));
			for(ModelResourceLocation mrl : funcPair.getRight()) {
				ModelBakery.registerItemVariants(blockItem, mrl);
			}
		}
		for(IMaterialFormItem materialFormItem : ItemFormType.getItems()) {
			Item item = materialFormItem.asItem();
			IItemFormSettings settings = (IItemFormSettings)materialFormItem.getForm().getSettings();
			Pair<Function<ItemStack, ModelResourceLocation>, Set<ModelResourceLocation>> funcPair =
					settings.getItemModelFunctionCreator().create(materialFormItem, settings);
			ModelLoader.setCustomMeshDefinition(item, s->funcPair.getLeft().apply(s));
			for(ModelResourceLocation mrl : funcPair.getRight()) {
				ModelBakery.registerItemVariants(item, mrl);
			}
		}
		for(IMaterialFormFluidBlock materialFormFluidBlock : FluidFormType.getFluidBlocks()) {
			Block fluidBlock = materialFormFluidBlock.asBlock();
			IFluidFormSettings settings = (IFluidFormSettings)materialFormFluidBlock.getForm().getSettings();
			ModelLoader.setCustomStateMapper(fluidBlock, b->settings.getFluidBlockModelMapCreator().create(materialFormFluidBlock, settings));
		}
	}
}
