package thelm.jaopca.compat.appliedenergistics2;

import appeng.items.materials.MaterialType;
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
		ApiImpl.INSTANCE.registerOredict("gemCertusQuartz", MaterialType.CERTUS_QUARTZ_CRYSTAL.stack(1));
		ApiImpl.INSTANCE.registerOredict("gemChargedCertusQuartz", MaterialType.CERTUS_QUARTZ_CRYSTAL_CHARGED.stack(1));
		ApiImpl.INSTANCE.registerOredict("gemSilicon", MaterialType.SILICON.stack(1));
		ApiImpl.INSTANCE.registerOredict("gemFluix", MaterialType.FLUIX_CRYSTAL.stack(1));
	}
}
