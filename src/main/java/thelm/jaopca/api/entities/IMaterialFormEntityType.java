package thelm.jaopca.api.entities;

import net.minecraftforge.fml.common.registry.EntityEntry;
import thelm.jaopca.api.materialforms.IMaterialForm;

public interface IMaterialFormEntityType extends IMaterialForm {

	default EntityEntry asEntityEntry() {
		return (EntityEntry)this;
	}
}
