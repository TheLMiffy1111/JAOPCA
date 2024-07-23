package thelm.jaopca.client.models;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.registries.ForgeRegistries;
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

	public static void registerModels(ModelEvent.RegisterAdditional event) {
		for(IMaterialFormBlock materialFormBlock : BlockFormType.getBlocks()) {
			Block block = materialFormBlock.toBlock();
			ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
			location = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			block.getStateDefinition().getPossibleStates().forEach((state)->{
				String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.BLOCKS.getKey(block), propertyMapString);
				ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
						JAOPCA.MOD_ID+':'+materialFormBlock.getMaterial().getModelType()+'/'+materialFormBlock.getForm().getName(),
						propertyMapString);
				event.register(defaultModelLocation);
				REMAPS.put(defaultModelLocation, modelLocation);
			});
		}
		for(IMaterialFormBlockItem materialFormBlockItem : BlockFormType.getBlockItems()) {
			BlockItem blockItem = materialFormBlockItem.toBlockItem();
			ResourceLocation location = ForgeRegistries.ITEMS.getKey(blockItem);
			location = new ResourceLocation(location.getNamespace(), "models/item/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(blockItem), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					JAOPCA.MOD_ID+':'+materialFormBlockItem.getMaterial().getModelType()+'/'+materialFormBlockItem.getForm().getName(),
					"inventory");
			event.register(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
		for(IMaterialFormItem materialFormItem : ItemFormType.getItems()) {
			Item item = materialFormItem.toItem();
			ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
			location = new ResourceLocation(location.getNamespace(), "models/item/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(item), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					JAOPCA.MOD_ID+':'+materialFormItem.getMaterial().getModelType()+'/'+materialFormItem.getForm().getName(),
					"inventory");
			event.register(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
		for(IMaterialFormFluidBlock materialFormFluidBlock : FluidFormType.getFluidBlocks()) {
			Block fluidBlock = materialFormFluidBlock.toBlock();
			ResourceLocation location = ForgeRegistries.BLOCKS.getKey(fluidBlock);
			location = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			fluidBlock.getStateDefinition().getPossibleStates().forEach((state)->{
				String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.BLOCKS.getKey(fluidBlock), propertyMapString);
				ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
						JAOPCA.MOD_ID+':'+materialFormFluidBlock.getMaterial().getModelType()+'/'+materialFormFluidBlock.getForm().getName(),
						propertyMapString);
				event.register(defaultModelLocation);
				REMAPS.put(defaultModelLocation, modelLocation);
			});
		}
		for(IMaterialFormBucketItem materialFormBucketItem : FluidFormType.getBucketItems()) {
			Item bucketItem = materialFormBucketItem.toItem();
			ResourceLocation location = ForgeRegistries.ITEMS.getKey(bucketItem);
			location = new ResourceLocation(location.getNamespace(), "models/item/"+location.getPath()+".json");
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(bucketItem), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					JAOPCA.MOD_ID+':'+materialFormBucketItem.getMaterial().getModelType()+'/'+materialFormBucketItem.getForm().getName(),
					"inventory");
			event.register(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
	}

	public static void remapModels(ModelEvent.BakingCompleted event) {
		Map<ResourceLocation, BakedModel> modelRegistry = event.getModels();
		BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
		for(Map.Entry<ResourceLocation, Collection<ResourceLocation>> entry : REMAPS.asMap().entrySet()) {
			BakedModel defaultModel = modelRegistry.getOrDefault(entry.getKey(), missingModel);
			for(ResourceLocation modelLocation : entry.getValue()) {
				modelRegistry.put(modelLocation, defaultModel);
			}
		}
	}
}
