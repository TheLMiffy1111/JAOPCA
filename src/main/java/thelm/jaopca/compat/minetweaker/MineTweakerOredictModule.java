package thelm.jaopca.compat.minetweaker;

import minetweaker.MineTweakerAPI;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;

@JAOPCAOredictModule(modDependencies = "MineTweaker3")
public class MineTweakerOredictModule implements IOredictModule {

	public MineTweakerOredictModule() {
		MineTweakerAPI.registerClass(JAOPCA.class);
		MineTweakerAPI.registerClass(Module.class);
		MineTweakerAPI.registerClass(Material.class);
		MineTweakerAPI.registerClass(Form.class);
		MineTweakerAPI.registerClass(MaterialForm.class);
	}

	@Override
	public String getName() {
		return "minetweaker";
	}

	@Override
	public void register() {}
}
