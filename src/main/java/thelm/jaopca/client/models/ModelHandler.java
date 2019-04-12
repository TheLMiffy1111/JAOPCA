package thelm.jaopca.client.models;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.blocks.BlockMaterialForm;
import thelm.jaopca.api.blocks.ItemBlockMaterialForm;
import thelm.jaopca.api.items.ItemMaterialForm;
import thelm.jaopca.api.materials.EnumMaterialType;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.items.ItemFormType;

public class ModelHandler {

	public static void remapModels(ModelBakeEvent event) {
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		Map<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		for(BlockMaterialForm block : BlockFormType.getBlocks()) {
			if(block.getMaterial().getType().isNone()) {
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
				IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(JAOPCA.MOD_ID+':'+block.getForm().getName(), propertyMapString));
				modelRegistry.put(modelLocation, defaultModel);
			});
		}
		for(ItemBlockMaterialForm itemBlock : BlockFormType.getItemBlocks()) {
			if(itemBlock.getMaterial().getType().isNone()) {
				continue;
			}
			//TODO Change if Forge supports using blockstates in item models
			ResourceLocation location = itemBlock.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || resourceExists(resourceManager, location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(itemBlock.getRegistryName(), "inventory");
			IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(JAOPCA.MOD_ID+':'+itemBlock.getForm().getName(), "inventory"));
			modelRegistry.put(modelLocation, defaultModel);
		}
		for(ItemMaterialForm item : ItemFormType.getItems()) {
			if(item.getMaterial().getType().isNone()) {
				continue;
			}
			ResourceLocation location = item.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || resourceExists(resourceManager, location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
			IBakedModel defaultModel = modelRegistry.get(new ModelResourceLocation(JAOPCA.MOD_ID+':'+item.getForm().getName(), "inventory"));
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
