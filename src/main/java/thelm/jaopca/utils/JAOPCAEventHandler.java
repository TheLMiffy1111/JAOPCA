package thelm.jaopca.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onBlockRegister(RegistryEvent.Register<Block> event) {
		
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onItemRegister(RegistryEvent.Register<Item> event) {
		
	}
	
	@SubscribeEvent
	public void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
		
	}
}
