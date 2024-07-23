package thelm.jaopca.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import thelm.jaopca.compat.kubejs.utils.JAOPCA;

public class JAOPCAKubeJSPlugin extends KubeJSPlugin {

	@Override
	public void registerClasses(ScriptType type, ClassFilter filter) {
		filter.allow("thelm.jaopca.api");
		filter.allow("thelm.jaopca.compat.kubejs.utils");
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("JAOPCA", JAOPCA.class);
		event.add("jaopca", JAOPCA.class);
	}
}
