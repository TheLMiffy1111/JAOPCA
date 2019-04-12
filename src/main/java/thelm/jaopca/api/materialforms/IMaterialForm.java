package thelm.jaopca.api.materialforms;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public interface IMaterialForm {

	IForm getForm();

	IMaterial getMaterial();

	void settingsChanged();
}
