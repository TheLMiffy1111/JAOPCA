package thelm.jaopca.api.blocks;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormBlock extends IMaterialForm {

	default Block toBlock() {
		return (Block)this;
	}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}

	default void initializeClient(Consumer<IClientBlockExtensions> consumer) {}

	default void addBlockModelRemaps(Set<ResourceLocation> allLocations, BiConsumer<ResourceLocation, ResourceLocation> output) {
		ResourceLocation location = BuiltInRegistries.BLOCK.getKey(toBlock());
		if(!allLocations.contains(location)) {
			output.accept(location, ResourceLocation.fromNamespaceAndPath("jaopca", getMaterial().getModelType()+'/'+getForm().getName()));
		}
	}
}
