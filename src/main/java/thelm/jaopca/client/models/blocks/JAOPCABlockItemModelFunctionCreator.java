package thelm.jaopca.client.models.blocks;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockItemModelFunctionCreator;
import thelm.jaopca.api.blocks.IMaterialFormBlockItem;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCABlockItemModelFunctionCreator implements IBlockItemModelFunctionCreator {

	public static final JAOPCABlockItemModelFunctionCreator INSTANCE = new JAOPCABlockItemModelFunctionCreator();

	@Override
	public Pair<Function<ItemStack, ModelResourceLocation>, Set<ModelResourceLocation>> create(IMaterialFormBlockItem blockItem, IBlockFormSettings settings) {
		ResourceLocation baseModelLocation = getBaseModelLocation(blockItem);
		ModelResourceLocation modelLocation = new ModelResourceLocation(baseModelLocation, "inventory");
		return Pair.of(s->modelLocation, Collections.singleton(modelLocation));
	}

	public ResourceLocation getBaseModelLocation(IMaterialFormBlockItem materialFormBlockItem) {
		return MiscHelper.INSTANCE.conditionalSupplier(FMLCommonHandler.instance().getSide()::isClient, ()->()->{
			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
			ItemBlock blockItem = materialFormBlockItem.asBlockItem();
			ResourceLocation location = blockItem.getRegistryName();
			ResourceLocation location1 = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
			ResourceLocation location2 = new ResourceLocation(location.getNamespace(), "models/item/"+location.getPath()+".json");
			ResourceLocation modelLocation;
			if(hasResource(resourceManager, location1) || hasResource(resourceManager, location2)) {
				return location;
			}
			else {
				return new ResourceLocation(location.getNamespace(),
						materialFormBlockItem.getMaterial().getModelType()+'/'+materialFormBlockItem.getForm().getName());
			}
		}, ()->materialFormBlockItem.asBlockItem()::getRegistryName).get();
	}

	public boolean hasResource(IResourceManager resourceManager, ResourceLocation location) {
		try {
			return resourceManager.getResource(location) != null;
		}
		catch(Exception e) {
			return false;
		}
	}
}
