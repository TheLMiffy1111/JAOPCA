package thelm.jaopca.client.models;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.items.ItemFormType;

public class ModelHandler {

	public static void remapModels(ModelBakeEvent event) {
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		for(IMaterialFormBlock materialFormBlock : BlockFormType.getBlocks()) {
			if(materialFormBlock.getMaterial().getType().isDummy()) {
				continue;
			}
			Block block = materialFormBlock.asBlock();
			ResourceLocation location = block.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
			if(false || resourceManager.hasResource(location)) {
				continue;
			}
			block.getStateContainer().getValidStates().forEach((state)->{
				String propertyMapString = BlockModelShapes.getPropertyMapString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(block.getRegistryName(), propertyMapString);
				String defaultModelLocation = JAOPCA.MOD_ID+':'+materialFormBlock.getMaterial().getTextureType().getRegistryName()+materialFormBlock.getForm().getName();
				IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(defaultModelLocation, propertyMapString));
				modelRegistry.put(modelLocation, defaultModel);
			});
		}
		for(IMaterialFormBlockItem materialFormBlockItem : BlockFormType.getBlockItems()) {
			if(materialFormBlockItem.getMaterial().getType().isDummy()) {
				continue;
			}
			BlockItem blockItem = materialFormBlockItem.asBlockItem();
			//TODO Change if Forge supports using blockstates in item models
			ResourceLocation location = blockItem.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || resourceManager.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(blockItem.getRegistryName(), "inventory");
			String defaultModelLocation = JAOPCA.MOD_ID+':'+materialFormBlockItem.getMaterial().getTextureType().getRegistryName()+materialFormBlockItem.getForm().getName();
			IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(defaultModelLocation, "inventory"));
			modelRegistry.put(modelLocation, defaultModel);
		}
		for(IMaterialFormItem materialFormItem : ItemFormType.getItems()) {
			if(materialFormItem.getMaterial().getType().isDummy()) {
				continue;
			}
			Item item = materialFormItem.asItem();
			ResourceLocation location = item.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || resourceManager.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
			String defaultModelLocation = JAOPCA.MOD_ID+':'+materialFormItem.getMaterial().getTextureType().getRegistryName()+materialFormItem.getForm().getName();
			IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(defaultModelLocation, "inventory"));
			modelRegistry.put(modelLocation, defaultModel);
		}
		for(IMaterialFormFluidBlock materialFormFluidBlock : FluidFormType.getFluidBlocks()) {
			if(materialFormFluidBlock.getMaterial().getType().isDummy()) {
				continue;
			}
			Block fluidBlock = materialFormFluidBlock.asBlock();
			ResourceLocation location = fluidBlock.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
			if(false || resourceManager.hasResource(location)) {
				continue;
			}
			fluidBlock.getStateContainer().getValidStates().forEach((state)->{
				String propertyMapString = BlockModelShapes.getPropertyMapString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(fluidBlock.getRegistryName(), propertyMapString);
				String defaultModelLocation = JAOPCA.MOD_ID+':'+materialFormFluidBlock.getMaterial().getTextureType().getRegistryName()+materialFormFluidBlock.getForm().getName();
				IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(defaultModelLocation, propertyMapString));
				modelRegistry.put(modelLocation, defaultModel);
			});
		}
		for(IMaterialFormBucketItem materialFormBucketItem : FluidFormType.getBucketItems()) {
			if(materialFormBucketItem.getMaterial().getType().isDummy()) {
				continue;
			}
			Item bucketItem = materialFormBucketItem.asItem();
			ResourceLocation location = bucketItem.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || resourceManager.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(bucketItem.getRegistryName(), "inventory");
			String defaultModelLocation = JAOPCA.MOD_ID+':'+materialFormBucketItem.getMaterial().getTextureType().getRegistryName()+materialFormBucketItem.getForm().getName();
			IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(defaultModelLocation, "inventory"));
			modelRegistry.put(modelLocation, defaultModel);
		}
	}
}
