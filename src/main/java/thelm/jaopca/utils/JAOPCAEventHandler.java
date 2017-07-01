package thelm.jaopca.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.JAOPCAApi;

public class JAOPCAEventHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {
		for(ResourceLocation location : JAOPCAApi.TEXTURES) {
			event.getMap().registerSprite(location);
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {

	}
}
