package thelm.jaopca.client.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.blocks.BlockMaterialForm;
import thelm.jaopca.api.blocks.MaterialFormBlockItem;
import thelm.jaopca.api.items.MaterialFormItem;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.items.ItemFormType;

public class ModelHandler {

	public static void remapModels(ModelBakeEvent event) {
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		for(BlockMaterialForm block : BlockFormType.getBlocks()) {
			if(block.getMaterial().getType().isDummy()) {
				continue;
			}
			ResourceLocation location = block.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
			if(false || resourceExists(resourceManager, location)) {
				continue;
			}
			block.getStateContainer().getValidStates().forEach((state)->{
				String propertyMapString = BlockModelShapes.getPropertyMapString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(block.getRegistryName(), propertyMapString);
				String defaultModelLocation = JAOPCA.MOD_ID+':'+block.getMaterial().getTextureType().getRegistryName()+block.getForm().getName();
				IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(defaultModelLocation, propertyMapString));
				modelRegistry.put(modelLocation, defaultModel);
			});
		}
		for(MaterialFormBlockItem itemBlock : BlockFormType.getBlockItems()) {
			if(itemBlock.getMaterial().getType().isDummy()) {
				continue;
			}
			//TODO Change if Forge supports using blockstates in item models
			ResourceLocation location = itemBlock.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || resourceExists(resourceManager, location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(itemBlock.getRegistryName(), "inventory");
			String defaultModelLocation = JAOPCA.MOD_ID+':'+itemBlock.getMaterial().getTextureType().getRegistryName()+itemBlock.getForm().getName();
			IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(defaultModelLocation, "inventory"));
			modelRegistry.put(modelLocation, defaultModel);
		}
		for(MaterialFormItem item : ItemFormType.getItems()) {
			if(item.getMaterial().getType().isDummy()) {
				continue;
			}
			ResourceLocation location = item.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || resourceExists(resourceManager, location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
			String defaultModelLocation = JAOPCA.MOD_ID+':'+item.getMaterial().getTextureType().getRegistryName()+item.getForm().getName();
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
