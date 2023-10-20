package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

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

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		builder.put("platedense", "dense_plate");
		return builder.build();
	}
}
