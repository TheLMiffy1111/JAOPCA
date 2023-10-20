package thelm.jaopca.modules.passive;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.material.Material;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAModule
public class MoltenModule implements IModule {

	private final IForm moltenForm = ApiImpl.INSTANCE.newForm(this, "molten", FluidFormType.INSTANCE).
			setMaterialTypes(MaterialType.NON_DUSTS).setSecondaryName("").
			setSettings(FluidFormType.INSTANCE.getNewSettings().
					setViscosityFunction(material->10000).setLuminosityFunction(material->10).
					setDensityFunction(material->2000).setTemperatureFunction(this::getTemperature).
					setMaterialFunction(material->Material.LAVA));

	private Map<IMaterial, IDynamicSpecConfig> configs;

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
		return Collections.singletonList(moltenForm.toRequest());
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		builder.put("molten", "molten");
		return builder.build();
	}

	public int getTemperature(IMaterial material) {
		return configs.get(material).getDefinedInt("molten.temperature", 1000, "The temperature of this molten fluid.");
	}
}
