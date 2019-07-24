package thelm.jaopca.client.resources;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.resources.InMemoryResourcePack;

public class ResourceInjector {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<ResourceLocation, Supplier<? extends JsonElement>> JSONS_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<String>> STRINGS_INJECT = new TreeMap<>();
	private static final TreeMap<ResourceLocation, Supplier<? extends InputStream>> INPUT_STREAMS_INJECT = new TreeMap<>();

	public static boolean injectJson(ResourceLocation location, Supplier<? extends JsonElement> supplier) {
		return JSONS_INJECT.putIfAbsent(location, supplier) == null;
	}

	public static boolean injectString(ResourceLocation location, Supplier<String> supplier) {
		return STRINGS_INJECT.putIfAbsent(location, supplier) == null;
	}

	public static boolean injectInputStream(ResourceLocation location, Supplier<? extends InputStream> supplier) {
		return INPUT_STREAMS_INJECT.putIfAbsent(location, supplier) == null;
	}

	public static class PackFinder implements IPackFinder {

		public static final PackFinder INSTANCE = new PackFinder();

		@Override
		public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packList, IFactory<T> factory) {
			T packInfo = ResourcePackInfo.createResourcePack("inmemory:jaopca", true, ()->{
				InMemoryResourcePack pack = new InMemoryResourcePack("inmemory:jaopca", true);
				JSONS_INJECT.forEach((location, supplier)->{
					pack.putJson(ResourcePackType.SERVER_DATA, location, supplier.get());
				});
				STRINGS_INJECT.forEach((location, supplier)->{
					pack.putString(ResourcePackType.SERVER_DATA, location, supplier.get());
				});
				INPUT_STREAMS_INJECT.forEach((location, supplier)->{
					pack.putInputStream(ResourcePackType.SERVER_DATA, location, supplier.get());
				});
				return pack;
			}, factory, ResourcePackInfo.Priority.BOTTOM);
		}
	}
}
