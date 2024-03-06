package thelm.jaopca.modules.passive;

import java.util.List;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.functions.MaterialIntFunction;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAModule
public class MoltenModule implements IModule {

	private final IForm moltenForm = ApiImpl.INSTANCE.newForm(this, "molten", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS).setTagSeparator("_").
			setSettings(FluidFormType.INSTANCE.getNewSettingsLava().
					setLightValueFunction(MaterialIntFunction.of(10)).setTickRateFunction(MaterialIntFunction.of(50)).
					setDensityFunction(MaterialIntFunction.of(10)).setViscosityFunction(MaterialIntFunction.of(10000)).
					setTemperatureFunction(MaterialIntFunction.of(1000, "molten.temperature", "The temperature of this molten fluid.")));

	@Override
	public String getName() {
		return "molten";
	}

	@Override
	public boolean isPassive() {
		return true;
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return List.of(moltenForm.toRequest());
	}
}
