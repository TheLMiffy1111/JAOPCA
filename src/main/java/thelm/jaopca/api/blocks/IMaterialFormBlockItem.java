package thelm.jaopca.api.blocks;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlockItem extends IMaterialForm {

	default BlockItem toBlockItem() {
		return (BlockItem)this;
	}

	default void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		output.accept(toBlockItem());
	}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}

	default void initializeClient(Consumer<IClientItemExtensions> consumer) {}

	default void addItemModelRemaps(Set<ResourceLocation> allLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {
		ResourceLocation location = BuiltInRegistries.ITEM.getKey(toBlockItem());
		if(!allLocations.contains(location)) {
			output.accept(location, ResourceLocation.fromNamespaceAndPath("jaopca", getMaterial().getModelType()+'/'+getForm().getName()));
		}
	}
}
