package thelm.jaopca.compat.kubejs;

import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassFilter;
import thelm.jaopca.compat.kubejs.utils.JAOPCA;

public class JAOPCAKubeJSPlugin extends KubeJSPlugin {

	@Override
	public void addClasses(ScriptType type, ClassFilter filter) {
		filter.allow("thelm.jaopca.api");
		filter.allow("thelm.jaopca.compat.kubejs.utils");
	}

	@Override
	public void addBindings(BindingsEvent event) {
		event.add("JAOPCA", JAOPCA.class);
		event.add("jaopca", JAOPCA.class);
	}
}
