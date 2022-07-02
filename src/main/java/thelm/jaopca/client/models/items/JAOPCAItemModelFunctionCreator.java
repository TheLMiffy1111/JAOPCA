package thelm.jaopca.client.models.items;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IItemModelFunctionCreator;
import thelm.jaopca.api.items.IMaterialFormItem;

public class JAOPCAItemModelFunctionCreator implements IItemModelFunctionCreator {

	public static final JAOPCAItemModelFunctionCreator INSTANCE = new JAOPCAItemModelFunctionCreator();

	@Override
	public Pair<Function<ItemStack, ModelResourceLocation>, Set<ModelResourceLocation>> create(IMaterialFormItem item, IItemFormSettings settings) {
		ResourceLocation baseModelLocation = getBaseModelLocation(item);
		ModelResourceLocation modelLocation = new ModelResourceLocation(baseModelLocation, "inventory");
		return Pair.of(s->modelLocation, Collections.singleton(modelLocation));
	}

	public ResourceLocation getBaseModelLocation(IMaterialFormItem materialFormItem) {
		IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
		Item item = materialFormItem.asItem();
		ResourceLocation location = item.getRegistryName();
		ResourceLocation location1 = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
		ResourceLocation location2 = new ResourceLocation(location.getNamespace(), "models/item/"+location.getPath()+".json");
		ResourceLocation modelLocation;
		if(hasResource(resourceManager, location1) || hasResource(resourceManager, location2)) {
			return location;
		}
		else {
			return new ResourceLocation(location.getNamespace(),
					materialFormItem.getMaterial().getModelType()+'/'+materialFormItem.getForm().getName());
		}
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
