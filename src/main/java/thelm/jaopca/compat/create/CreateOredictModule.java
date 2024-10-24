package thelm.jaopca.compat.create;

import com.siepert.createlegacy.util.compat.OreDictionaryCompat;

import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;

@JAOPCAOredictModule(modDependencies = "create")
public class CreateOredictModule implements IOredictModule {

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public void register() {
		OreDictionaryCompat.registerOres();
		OreDictionaryCompat.registerStoneTypes();
	}
}
