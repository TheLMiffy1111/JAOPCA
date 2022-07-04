package thelm.jaopca.compat.taiga;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;

@JAOPCAOredictModule(modDependencies = "taiga")
public class TAIGAOredictModule implements IOredictModule {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public String getName() {
		return "taiga";
	}

	@Override
	public void register() {
		try {
			Class.forName("com.sosnitzka.taiga.Blocks").getMethod("register", boolean.class).invoke(null, true);
		}
		catch(Exception e) {
			LOGGER.error("Could not invoke TAIGA oredict register method", e);
		}
	}
}
