package thelm.jaopca.utils;

import java.io.File;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.ore.OreFinder;
import thelm.jaopca.registry.RegistryCore;
import thelm.wrapup.event.InitializationWrapUpEvent;
import thelm.wrapup.event.PostInitializationWrapUpEvent;
import thelm.wrapup.event.PreInitializationWrapUpEvent;

public class JAOPCAEventHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {
		for(ResourceLocation location : JAOPCAApi.TEXTURES) {
			event.getMap().registerSprite(location);
		}
	}

	@SubscribeEvent
	public void onPreInitWrapUp(PreInitializationWrapUpEvent.Event2 event) {
		JAOPCAConfig.init(new File(event.event.getModConfigurationDirectory(), "JAOPCA.cfg"));
		OreFinder.findOres();
		RegistryCore.preInit();
	}

	@SubscribeEvent
	public void onInitWrapUp(InitializationWrapUpEvent.Event2 event) {
		RegistryCore.init();
	}

	@SubscribeEvent
	public void onPostInitWrapUp(PostInitializationWrapUpEvent.Event2 event) {
		RegistryCore.postInit();
	}
}
