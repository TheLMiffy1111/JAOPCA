package thelm.jaopca.client.models;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.blocks.BlockFormType;
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
			if(false || resourceExists(resourceManager, location)) {
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
			if(false || resourceExists(resourceManager, location)) {
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
			if(false || resourceExists(resourceManager, location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
			String defaultModelLocation = JAOPCA.MOD_ID+':'+materialFormItem.getMaterial().getTextureType().getRegistryName()+materialFormItem.getForm().getName();
			IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(defaultModelLocation, "inventory"));
			modelRegistry.put(modelLocation, defaultModel);
		}
	}

	static boolean resourceExists(IResourceManager resourceManager, ResourceLocation location) {
		try {
			resourceManager.getResource(location);
			return true;
		}
		catch(FileNotFoundException e) {
			return false;
		}
		catch(IOException e) {
			return false;
		}
	}
}
