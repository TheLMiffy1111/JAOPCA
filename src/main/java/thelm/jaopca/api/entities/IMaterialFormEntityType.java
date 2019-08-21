package thelm.jaopca.api.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormEntityType<E extends Entity> extends IMaterialForm {

	default EntityType<E> asEntityType() {
		return (EntityType<E>)this;
	}
}
