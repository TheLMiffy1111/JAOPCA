package thelm.jaopca.api.items;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormItem extends IMaterialForm {

	default Item toItem() {
		return (Item)this;
	}

	default void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		output.accept(toItem());
	}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}

	default void initializeClient(Consumer<IClientItemExtensions> consumer) {}

	default void addItemModelRemaps(Set<ResourceLocation> allLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {
		ResourceLocation location = BuiltInRegistries.ITEM.getKey(toItem());
		if(!allLocations.contains(location)) {
			output.accept(location, ResourceLocation.fromNamespaceAndPath("jaopca", getMaterial().getModelType()+'/'+getForm().getName()));
		}
	}
}
