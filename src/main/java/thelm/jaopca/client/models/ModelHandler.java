package thelm.jaopca.client.models;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.registries.ForgeRegistries;
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

	private static final FileToIdConverter BLOCK_MODEL_FORMAT = FileToIdConverter.json("blockstates");
	private static final FileToIdConverter ITEM_MODEL_FORMAT = FileToIdConverter.json("models/item");
	private static final Multimap<ResourceLocation, ResourceLocation> REMAPS = LinkedHashMultimap.create();

	public static void registerModels(ModelEvent.RegisterAdditional event) {
		for(IMaterialFormBlock materialFormBlock : BlockFormType.getBlocks()) {
			Block block = materialFormBlock.toBlock();
			ResourceLocation location = BLOCK_MODEL_FORMAT.idToFile(ForgeRegistries.BLOCKS.getKey(block));
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			block.getStateDefinition().getPossibleStates().forEach((state)->{
				String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.BLOCKS.getKey(block), propertyMapString);
				ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
						"jaopca",
						materialFormBlock.getMaterial().getModelType()+'/'+materialFormBlock.getForm().getName(),
						propertyMapString);
				event.register(defaultModelLocation);
				REMAPS.put(defaultModelLocation, modelLocation);
			});
		}
		for(IMaterialFormBlockItem materialFormBlockItem : BlockFormType.getBlockItems()) {
			BlockItem blockItem = materialFormBlockItem.toBlockItem();
			ResourceLocation location = ITEM_MODEL_FORMAT.idToFile(ForgeRegistries.ITEMS.getKey(blockItem));
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(blockItem), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					"jaopca",
					materialFormBlockItem.getMaterial().getModelType()+'/'+materialFormBlockItem.getForm().getName(),
					"inventory");
			event.register(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
		for(IMaterialFormItem materialFormItem : ItemFormType.getItems()) {
			Item item = materialFormItem.toItem();
			ResourceLocation location = ITEM_MODEL_FORMAT.idToFile(ForgeRegistries.ITEMS.getKey(item));
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(item), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					"jaopca",
					materialFormItem.getMaterial().getModelType()+'/'+materialFormItem.getForm().getName(),
					"inventory");
			event.register(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
		for(IMaterialFormFluidBlock materialFormFluidBlock : FluidFormType.getFluidBlocks()) {
			Block fluidBlock = materialFormFluidBlock.toBlock();
			ResourceLocation location = BLOCK_MODEL_FORMAT.idToFile(ForgeRegistries.BLOCKS.getKey(fluidBlock));
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			fluidBlock.getStateDefinition().getPossibleStates().forEach((state)->{
				String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
				ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.BLOCKS.getKey(fluidBlock), propertyMapString);
				ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
						"jaopca",
						materialFormFluidBlock.getMaterial().getModelType()+'/'+materialFormFluidBlock.getForm().getName(),
						propertyMapString);
				event.register(defaultModelLocation);
				REMAPS.put(defaultModelLocation, modelLocation);
			});
		}
		for(IMaterialFormBucketItem materialFormBucketItem : FluidFormType.getBucketItems()) {
			Item bucketItem = materialFormBucketItem.toItem();
			ResourceLocation location = ITEM_MODEL_FORMAT.idToFile(ForgeRegistries.ITEMS.getKey(bucketItem));
			if(false || MiscHelper.INSTANCE.hasResource(location)) {
				continue;
			}
			ModelResourceLocation modelLocation = new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(bucketItem), "inventory");
			ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
					"jaopca",
					materialFormBucketItem.getMaterial().getModelType()+'/'+materialFormBucketItem.getForm().getName(),
					"inventory");
			event.register(defaultModelLocation);
			REMAPS.put(defaultModelLocation, modelLocation);
		}
	}

	public static void remapModels(ModelEvent.ModifyBakingResult event) {
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
