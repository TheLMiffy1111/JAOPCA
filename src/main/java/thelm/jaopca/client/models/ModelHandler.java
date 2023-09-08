package thelm.jaopca.client.models;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.MiscHelper;

public class ModelHandler {

	private static final Multimap<ResourceLocation, ResourceLocation> REMAPS = LinkedHashMultimap.create();

	public static void registerModels() {
		for(IMaterialFormBlock materialFormBlock : BlockFormType.getBlocks()) {
			Block block = materialFormBlock.toBlock();
			ResourceLocation location = block.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			block.getStateDefinition().getPossibleStates().forEach((state)->{
				String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(block.getRegistryName(), propertyMapString);
				ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
						JAOPCA.MOD_ID+':'+materialFormBlock.getMaterial().getModelType()+'/'+materialFormBlock.getForm().getName(),
						propertyMapString);
				ForgeModelBakery.addSpecialModel(defaultModelLocation);
				REMAPS.put(defaultModelLocation, modelLocation);
			});
		}
		for(IMaterialFormBlockItem materialFormBlockItem : BlockFormType.getBlockItems()) {
			BlockItem blockItem = materialFormBlockItem.toBlockItem();
			//TODO Change if Forge supports using blockstates in item models
			ResourceLocation location = blockItem.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(blockItem.getRegistryName(), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					JAOPCA.MOD_ID+':'+materialFormBlockItem.getMaterial().getModelType()+'/'+materialFormBlockItem.getForm().getName(),
					"inventory");
			ForgeModelBakery.addSpecialModel(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
		for(IMaterialFormItem materialFormItem : ItemFormType.getItems()) {
			Item item = materialFormItem.toItem();
			ResourceLocation location = item.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					JAOPCA.MOD_ID+':'+materialFormItem.getMaterial().getModelType()+'/'+materialFormItem.getForm().getName(),
					"inventory");
			ForgeModelBakery.addSpecialModel(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
		for(IMaterialFormFluidBlock materialFormFluidBlock : FluidFormType.getFluidBlocks()) {
			Block fluidBlock = materialFormFluidBlock.toBlock();
			ResourceLocation location = fluidBlock.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			fluidBlock.getStateDefinition().getPossibleStates().forEach((state)->{
				String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(fluidBlock.getRegistryName(), propertyMapString);
				ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
						JAOPCA.MOD_ID+':'+materialFormFluidBlock.getMaterial().getModelType()+'/'+materialFormFluidBlock.getForm().getName(),
						propertyMapString);
				ForgeModelBakery.addSpecialModel(defaultModelLocation);
				REMAPS.put(defaultModelLocation, modelLocation);
			});
		}
		for(IMaterialFormBucketItem materialFormBucketItem : FluidFormType.getBucketItems()) {
			Item bucketItem = materialFormBucketItem.toItem();
			ResourceLocation location = bucketItem.getRegistryName();
			location = new ResourceLocation(location.getNamespace(), "item/models/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(bucketItem.getRegistryName(), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					JAOPCA.MOD_ID+':'+materialFormBucketItem.getMaterial().getModelType()+'/'+materialFormBucketItem.getForm().getName(),
					"inventory");
			ForgeModelBakery.addSpecialModel(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
	}

	public static void remapModels(ModelBakeEvent event) {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
		BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
		for(Map.Entry<ResourceLocation, Collection<ResourceLocation>> entry : REMAPS.asMap().entrySet()) {
			BakedModel defaultModel = modelRegistry.getOrDefault(entry.getKey(), missingModel);
			for(ResourceLocation modelLocation : entry.getValue()) {
				modelRegistry.put(modelLocation, defaultModel);
			}
		}
	}
}
