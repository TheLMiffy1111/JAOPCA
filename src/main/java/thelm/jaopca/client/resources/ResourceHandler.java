package thelm.jaopca.client.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public class ResourceHandler {

	private static final Multimap<Integer, Pair<Supplier<List<String>>, Consumer<List<IIcon>>>> TEXTURE_SUPPLIERS = MultimapBuilder.hashKeys().arrayListValues().build();

	public static void registerTextures(int mapId, Supplier<List<String>> supplier, Consumer<List<IIcon>> consumer) {
		TEXTURE_SUPPLIERS.put(mapId, Pair.of(supplier, consumer));
	}

	public static void registerTextures(TextureMap map) {
		for(Pair<Supplier<List<String>>, Consumer<List<IIcon>>> pair : TEXTURE_SUPPLIERS.get(map.getTextureType())) {
			List<IIcon> list = new ArrayList<>();
			for(String location : pair.getLeft().get()) {
				list.add(map.registerIcon(location));
			}
			pair.getRight().accept(list);
		}
	}
}
