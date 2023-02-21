package thelm.jaopca.compat.hbm.ntmmaterials;

import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.forms.IFormType;

public class NTMMaterialFormSettings implements IFormSettings {

	NTMMaterialFormSettings() {}

	@Override
	public IFormType getType() {
		return NTMMaterialFormType.INSTANCE;
	}
}
