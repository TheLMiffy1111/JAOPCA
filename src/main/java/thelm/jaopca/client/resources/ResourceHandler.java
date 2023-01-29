package thelm.jaopca.client.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class ResourceHandler {

	private static final List<Supplier<List<ResourceLocation>>> TEXTURE_SUPPLIERS = new ArrayList<>();

	public static void registerTextures(Supplier<List<ResourceLocation>> supplier) {
		TEXTURE_SUPPLIERS.add(supplier);
	}

	public static void registerTextures(TextureMap map) {
		for(Supplier<List<ResourceLocation>> supplier : TEXTURE_SUPPLIERS) {
			for(ResourceLocation location : supplier.get()) {
				map.registerSprite(location);
			}
		}
	}
}
