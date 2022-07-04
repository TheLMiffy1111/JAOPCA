package thelm.jaopca.compat.calculator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;

@JAOPCAOredictModule(modDependencies = "calculator")
public class CalculatorOredictModule implements IOredictModule {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public String getName() {
		return "calculator";
	}

	@Override
	public void register() {
		try {
			Class.forName("sonar.calculator.mod.CalculatorOreDict").getMethod("registerOres").invoke(null);
		}
		catch(Exception e) {
			LOGGER.error("Could not invoke Calculator oredict register method", e);
		}
	}
}
