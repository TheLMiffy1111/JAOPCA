package thelm.jaopca.resources;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.resources.IInMemoryResourcePack;

public class InMemoryResourcePack implements IInMemoryResourcePack {

	private static final Gson GSON = new GsonBuilder().create();
	private final String name;
	private final boolean isHidden;
	private final JsonObject metadata = (JsonObject)new JsonParser().parse("{\"pack_format\":4,\"description\":\"JAOPCA In Memory Resources\"}");
	private final TreeMap<String, Supplier<? extends InputStream>> root = new TreeMap<>();
	private final TreeMap<ResourceLocation, Supplier<? extends InputStream>> assets = new TreeMap<>();
	private final TreeMap<ResourceLocation, Supplier<? extends InputStream>> data = new TreeMap<>();

	public InMemoryResourcePack(String name, boolean isHidden) {
		this.name = name;
		this.isHidden = isHidden;
	}

	@Override
	public IInMemoryResourcePack putInputStream(ResourcePackType type, ResourceLocation location, Supplier<? extends InputStream> streamSupplier) {
		switch(type) {
		case CLIENT_RESOURCES:
			assets.put(location, streamSupplier);
			break;
		case SERVER_DATA:
			data.put(location, streamSupplier);
			break;
		default:
			break;
		}
		return this;
	}

	@Override
	public IInMemoryResourcePack putInputStreams(ResourcePackType type, Map<ResourceLocation, Supplier<? extends InputStream>> map) {
		switch(type) {
		case CLIENT_RESOURCES:
			assets.putAll(map);
			break;
		case SERVER_DATA:
			data.putAll(map);
			break;
		default:
			break;
		}
		return this;
	}

	@Override
	public IInMemoryResourcePack putByteArray(ResourcePackType type, ResourceLocation location, byte[] file) {
		return putInputStream(type, location, ()->new ByteArrayInputStream(file));
	}

	@Override
	public IInMemoryResourcePack putByteArrays(ResourcePackType type, Map<ResourceLocation, byte[]> map) {
		return putInputStreams(type, Maps.transformValues(map, file->()->new ByteArrayInputStream(file)));
	}

	@Override
	public IInMemoryResourcePack putString(ResourcePackType type, ResourceLocation location, String str) {
		return putByteArray(type, location, str.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public IInMemoryResourcePack putStrings(ResourcePackType type, Map<ResourceLocation, String> map) {
		return putByteArrays(type, Maps.transformValues(map, str->str.getBytes(StandardCharsets.UTF_8)));
	}

	@Override
	public IInMemoryResourcePack putJson(ResourcePackType type, ResourceLocation location, JsonElement json) {
		return putString(type, location, GSON.toJson(json));
	}

	@Override
	public IInMemoryResourcePack putJsons(ResourcePackType type, Map<ResourceLocation, ? extends JsonElement> map) {
		return putStrings(type, Maps.transformValues(map, json->GSON.toJson(json)));
	}

	@Override
	public InputStream getRootResourceStream(String fileName) throws IOException {
		if(fileName.contains("/") || fileName.contains("\\")) {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
		if(root.containsKey(fileName)) {
			return root.get(fileName).get();
		}
		throw new FileNotFoundException(fileName);
	}

	@Override
	public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
		Map<ResourceLocation, Supplier<? extends InputStream>> map = type == ResourcePackType.CLIENT_RESOURCES ? assets : data;
		if(map.containsKey(location)) {
			return map.get(location).get();
		}
		throw new FileNotFoundException(location.toString());
	}

	@Override
	public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespace, String pathIn, int maxDepth, Predicate<String> filter) {
		Map<ResourceLocation, Supplier<? extends InputStream>> map = type == ResourcePackType.CLIENT_RESOURCES ? assets : data;
		return map.keySet().stream().filter(rl->rl.getNamespace().equals(namespace)).
				filter(rl->StringUtils.countMatches(rl.getPath(), '/') < maxDepth).
				filter(rl->rl.getPath().startsWith(pathIn)).filter(rl->{
					String path = rl.getPath();
					int lastSlash = path.lastIndexOf('/');
					return filter.test(path.substring(lastSlash < 0 ? 0 : lastSlash, path.length()));
				}).collect(Collectors.toList());
	}

	@Override
	public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
		Map<ResourceLocation, Supplier<? extends InputStream>> map = type == ResourcePackType.CLIENT_RESOURCES ? assets : data;
		return map.containsKey(location);
	}

	@Override
	public Set<String> getResourceNamespaces(ResourcePackType type) {
		Map<ResourceLocation, Supplier<? extends InputStream>> map = type == ResourcePackType.CLIENT_RESOURCES ? assets : data;
		return map.keySet().stream().map(rl->rl.getNamespace()).collect(Collectors.toSet());
	}

	@Override
	public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
		return deserializer.deserialize(metadata);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isHidden() {
		return isHidden;
	}

	@Override
	public void close() throws IOException {}
}
