package thelm.oredictinit.compat;

import java.lang.reflect.Method;

import thelm.jaopca.api.ICompat;

public class CompatCalculator implements ICompat {

	@Override
	public String getName() {
		return "calculator";
	}

	@Override
	public void register() {
		try {
			Class<?> oreClass = Class.forName("sonar.calculator.mod.CalculatorOreDict");
			Method registerMethod = oreClass.getMethod("registerOres");
			registerMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
