package thelm.jaopca.api.entities;

import net.minecraftforge.fml.common.registry.EntityEntry;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public interface IEntityEntryInfo extends IMaterialFormInfo {

	IMaterialFormEntityType getMaterialFormEntityEntry();

	default EntityEntry getEntityEntry() {
		return getMaterialFormEntityEntry().asEntityEntry();
	}

	@Override
	default IMaterialFormEntityType getMaterialForm() {
		return getMaterialFormEntityEntry();
	}
}
