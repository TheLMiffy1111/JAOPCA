package thelm.jaopca.client.events;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.client.renderer.JAOPCABlockRenderer;
import thelm.jaopca.client.resources.ResourceHandler;
import thelm.jaopca.events.CommonEventHandler;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.localization.LocalizationRepoHandler;

public class ClientEventHandler extends CommonEventHandler {

	@Override
	public void onInit(FMLInitializationEvent event) {
		super.onInit(event);
		Minecraft mc = Minecraft.getMinecraft();
		LocalizationRepoHandler.setup(modConfigDir);
		((IReloadableResourceManager)mc.getResourceManager()).registerReloadListener(new IResourceManagerReloadListener() {
			@Override
			public void onResourceManagerReload(IResourceManager resourceManager) {
				LocalizationRepoHandler.reload();
			}
		});
	}

	@Override
	public void onPostInit(FMLPostInitializationEvent event) {
		super.onPostInit(event);
		RenderingRegistry.registerBlockHandler(JAOPCABlockRenderer.INSTANCE);
		for(IMaterialFormItem item : ItemFormType.getItems()) {
			IItemRenderer renderer = item.getRenderer();
			if(renderer != null) {
				MinecraftForgeClient.registerItemRenderer(item.toItem(), renderer);
			}
		}
	}

	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {
		ResourceHandler.registerTextures(event.map);
	}
}
