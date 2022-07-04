package thelm.jaopca.client.resources;

import java.util.TreeSet;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class ResourceHandler {

	private static final TreeSet<ResourceLocation> TEXTURES = new TreeSet<>();

	public static void registerTexture(ResourceLocation location) {
		TEXTURES.add(location);
	}

	public static void registerTextures(TextureMap map) {
		for(ResourceLocation location : TEXTURES) {
			map.registerSprite(location);
		}
	}
}
