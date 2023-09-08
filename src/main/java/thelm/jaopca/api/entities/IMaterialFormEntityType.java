package thelm.jaopca.api.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormEntityType<E extends Entity> extends IMaterialForm {

	default EntityType<E> toEntityType() {
		return (EntityType<E>)this;
	}
}
