package thelm.jaopca.compat.appliedenergistics2;

import appeng.items.materials.MaterialType;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "appliedenergistics2")
public class AppliedEnergistics2OredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "appliedenergistics2";
	}

	@Override
	public void register() {
		JAOPCAApi api = ApiImpl.INSTANCE;
		api.registerOredict("gemCertusQuartz", MaterialType.CertusQuartzCrystal.stack(1));
		api.registerOredict("gemChargedCertusQuartz", MaterialType.CertusQuartzCrystalCharged.stack(1));
		api.registerOredict("gemSilicon", MaterialType.Silicon.stack(1));
		api.registerOredict("gemFluix", MaterialType.FluixCrystal.stack(1));
	}
}
