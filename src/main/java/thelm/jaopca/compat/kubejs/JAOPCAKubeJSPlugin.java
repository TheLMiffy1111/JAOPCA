package thelm.jaopca.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import thelm.jaopca.compat.kubejs.utils.JAOPCA;

public class JAOPCAKubeJSPlugin implements KubeJSPlugin {

	@Override
	public void registerClasses(ClassFilter filter) {
		filter.allow("thelm.jaopca.api");
		filter.allow("thelm.jaopca.compat.kubejs.utils");
	}

	@Override
	public void registerBindings(BindingRegistry registry) {
		registry.add("JAOPCA", JAOPCA.class);
		registry.add("jaopca", JAOPCA.class);
	}
}
