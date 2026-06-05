package thelm.jaopca.api.fluids;

import java.util.Set;
import java.util.function.BiConsumer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormFluidBlock extends IMaterialForm {

	default Block toBlock() {
		return (Block)this;
	}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}

	default void addBlockModelRemaps(Set<Identifier> allLocations, BiConsumer<Identifier, Identifier> output) {
		Identifier location = BuiltInRegistries.BLOCK.getKey(toBlock());
		if(!allLocations.contains(location)) {
			output.accept(location, Identifier.fromNamespaceAndPath("jaopca", getMaterial().getModelType()+'/'+getForm().getName()));
		}
	}
}
