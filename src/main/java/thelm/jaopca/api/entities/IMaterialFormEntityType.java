package thelm.jaopca.api.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormEntityType<E extends Entity> extends IMaterialForm {

	default EntityType<E> asEntityType() {
		return (EntityType<E>)this;
	}

	default void onRegisterCapabilities(RegisterCapabilitiesEvent event) {}
}
