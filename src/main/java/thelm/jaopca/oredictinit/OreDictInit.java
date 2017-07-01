package thelm.jaopca.oredictinit;

import java.io.File;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import thelm.jaopca.oredictinit.lib.Compat;
import thelm.jaopca.oredictinit.lib.ConfigHandler;
import thelm.jaopca.oredictinit.registry.OreDictRegisCore;

public class OreDictInit {
	
	//Everything in this package was originally its own mod, decided to move it here.
	
	public static OreDictInit instance = new OreDictInit();
	
	public void preInit(FMLPreInitializationEvent event) {
		Woodchopper.info("Initializing...");
		
		Woodchopper.debug("Generating and Registering Config File");
		ConfigHandler.preInit(new File(event.getModConfigurationDirectory(), "OreDictInit.cfg"));
		
		Woodchopper.debug("Registering Compats");
		Compat.init();
		
		OreDictRegisCore.initCompat();
		OreDictRegisCore.initCustom();
		
		Woodchopper.info("Done!");
	}
}
