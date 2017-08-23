package thelm.oredictinit.lib;

import java.io.File;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thelm.oredictinit.Woodchopper;
import thelm.oredictinit.registry.OreDictRegisCore;
import thelm.wrapup.event.PreInitializationWrapUpEvent;
import thelm.wrapup.event.RegistryWrapUpEvent;

public class EventHandler {
	
	@SubscribeEvent
	public void onPreInitWrapUp(PreInitializationWrapUpEvent.Event0 event) {
		Woodchopper.debug("Generating and Registering Config File");
		ConfigHandler.preInit(new File(event.event.getModConfigurationDirectory(), "OreDictInit.cfg"));

		Woodchopper.debug("Registering Compats");
		Compat.init();
	}
	
	@SubscribeEvent
	public void onRegistryWrapUp(RegistryWrapUpEvent.Event0 event) {
		OreDictRegisCore.initCompat();
		OreDictRegisCore.initCustom();
	}
}
