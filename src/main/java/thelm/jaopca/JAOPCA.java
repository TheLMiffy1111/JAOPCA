package thelm.jaopca;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import thelm.jaopca.events.CommonEventHandler;

@Mod(
		modid = JAOPCA.MOD_ID,
		name = JAOPCA.NAME,
		version = JAOPCA.VERSION,
		dependencies = JAOPCA.DEPENDENCIES
		)
public class JAOPCA {

	public static final String MOD_ID = "jaopca";
	public static final String NAME = "JAOPCA";
	public static final String VERSION = "1.7.10-0@VERSION@";
	public static final String DEPENDENCIES = "after:*";
	@Instance(JAOPCA.MOD_ID)
	public static JAOPCA core;
	@SidedProxy(
			clientSide = "thelm.jaopca.client.events.ClientEventHandler",
			serverSide = "thelm.jaopca.events.CommonEventHandler",
			modId = JAOPCA.MOD_ID)
	public static CommonEventHandler eventHandler;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(eventHandler);
		eventHandler.onPreInit(event);
	}

	@EventHandler
	public void onInit(FMLInitializationEvent event) {
		eventHandler.onInit(event);
	}

	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event) {
		eventHandler.onPostInit(event);
	}

	@EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent event) {
		eventHandler.onLoadComplete(event);
	}
}
