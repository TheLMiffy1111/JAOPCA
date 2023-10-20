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
public class CoinModule implements IModule {

	private final IForm coinForm = ApiImpl.INSTANCE.newForm(this, "coin", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOTS);

	@Override
	public String getName() {
		return "coin";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(coinForm.toRequest());
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		builder.put("coin", "coin");
		return builder.build();
	}
}
