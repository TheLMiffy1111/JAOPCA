package thelm.jaopca.api.blocks;

import java.util.Set;
import java.util.function.BiConsumer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlockItem extends IMaterialForm {

	default BlockItem toBlockItem() {
		return (BlockItem)this;
	}

	default void addToCreativeModeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
		output.accept(toBlockItem());
	}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}

	default void addItemModelRemaps(Set<Identifier> allLocations, BiConsumer<Identifier, Identifier> output) {
		Identifier location = BuiltInRegistries.ITEM.getKey(toBlockItem());
		if(!allLocations.contains(location)) {
			output.accept(location, Identifier.fromNamespaceAndPath("jaopca", getMaterial().getModelType()+'/'+getForm().getName()));
		}
	}
}
