package thelm.jaopca.compat.hbm.ntmmaterials;

import thelm.jaopca.api.materialforms.IMaterialFormInfo;

public class NTMMaterialInfo implements IMaterialFormInfo {

	private final JAOPCANTMMaterial ntmMaterial;

	NTMMaterialInfo(JAOPCANTMMaterial ntmMaterial) {
		this.ntmMaterial = ntmMaterial;
	}

	@Override
	public JAOPCANTMMaterial getMaterialForm() {
		return ntmMaterial;
	}
}
