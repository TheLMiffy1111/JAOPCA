package thelm.jaopca.api.items;

import java.util.Set;
import java.util.function.BiConsumer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormItem extends IMaterialForm {

	default Item toItem() {
		return (Item)this;
	}

	default void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		output.accept(toItem());
	}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}

	default void addItemModelRemaps(Set<Identifier> allLocations, BiConsumer<Identifier, Identifier> output) {
		Identifier location = BuiltInRegistries.ITEM.getKey(toItem());
		if(!allLocations.contains(location)) {
			output.accept(location, Identifier.fromNamespaceAndPath("jaopca", getMaterial().getModelType()+'/'+getForm().getName()));
		}
	}
}
