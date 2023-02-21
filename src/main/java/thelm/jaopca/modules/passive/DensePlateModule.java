package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAModule
public class DensePlateModule implements IModule {

	private final IForm densePlateForm = ApiImpl.INSTANCE.newForm(this, "dense_plate", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS).setSecondaryName("plateDense");

	@Override
	public String getName() {
		return "dense_plate";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(densePlateForm.toRequest());
	}
}
