package thelm.jaopca.resources;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import thelm.jaopca.api.resources.IInMemoryResourcePack;

public class InMemoryResourcePack implements IInMemoryResourcePack {

	private static final Gson GSON = new GsonBuilder().create();
	private final String packId;
	private final boolean isHidden;
	private final JsonObject metadata = (JsonObject)JsonParser.parseString("{\"pack_format\":4,\"description\":\"JAOPCA In Memory Resources\"}");
	private final TreeMap<String, Supplier<? extends InputStream>> files = new TreeMap<>();

	public InMemoryResourcePack(String packId, boolean isHidden) {
		this.packId = packId;
		this.isHidden = isHidden;
	}

	private static String getPath(PackType packType, ResourceLocation location) {
		return String.format(Locale.ROOT, "%s/%s/%s", packType.getDirectory(), location.getNamespace(), location.getPath());
	}

	@Override
	public IInMemoryResourcePack putInputStream(PackType type, ResourceLocation location, Supplier<? extends InputStream> streamSupplier) {
		files.put(getPath(type, location), streamSupplier);
		return this;
	}

	@Override
	public IInMemoryResourcePack putInputStreams(PackType type, Map<ResourceLocation, Supplier<? extends InputStream>> map) {
		map.forEach((location, streamSupplier)->{
			files.put(getPath(type, location), streamSupplier);
		});
		return this;
	}

	@Override
	public IInMemoryResourcePack putByteArray(PackType type, ResourceLocation location, byte[] file) {
		return putInputStream(type, location, ()->new ByteArrayInputStream(file));
	}

	@Override
	public IInMemoryResourcePack putByteArrays(PackType type, Map<ResourceLocation, byte[]> map) {
		return putInputStreams(type, Maps.transformValues(map, file->()->new ByteArrayInputStream(file)));
	}

	@Override
	public IInMemoryResourcePack putString(PackType type, ResourceLocation location, String str) {
		return putByteArray(type, location, str.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public IInMemoryResourcePack putStrings(PackType type, Map<ResourceLocation, String> map) {
		return putByteArrays(type, Maps.transformValues(map, str->str.getBytes(StandardCharsets.UTF_8)));
	}

	@Override
	public IInMemoryResourcePack putJson(PackType type, ResourceLocation location, JsonElement json) {
		return putString(type, location, GSON.toJson(json));
	}

	@Override
	public IInMemoryResourcePack putJsons(PackType type, Map<ResourceLocation, ? extends JsonElement> map) {
		return putStrings(type, Maps.transformValues(map, json->GSON.toJson(json)));
	}

	@Override
	public IoSupplier<InputStream> getRootResource(String... path) {
		String filePath = String.join("/", path);
		return ()->{
			if(files.containsKey(filePath)) {
				return files.get(filePath).get();
			}
			throw new FileNotFoundException(filePath);
		};
	}

	@Override
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		String filePath = getPath(type, location);
		return ()->{
			if(files.containsKey(filePath)) {
				return files.get(filePath).get();
			}
			throw new FileNotFoundException(filePath);
		};
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput resourceOutput) {
		String prefix = type.getDirectory()+'/'+namespace+'/';
		String prefix1 = prefix+'/'+path+'/';
		files.forEach((filePath, streamSupplier)->{
			if(filePath.startsWith(prefix1)) {
				resourceOutput.accept(new ResourceLocation(namespace, filePath.substring(prefix.length())), streamSupplier::get);
			}
		});
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		String prefix = type.getDirectory()+'/';
		return files.keySet().stream().
				filter(filePath->filePath.startsWith(prefix)).
				map(filePath->filePath.substring(prefix.length())).
				filter(filePath->filePath.contains("/")).
				map(filePath->filePath.substring(0, filePath.indexOf("/"))).
				collect(Collectors.toSet());
	}

	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
		return deserializer == PackMetadataSection.TYPE ? deserializer.fromJson(metadata) : null;
	}

	@Override
	public String packId() {
		return packId;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public boolean isHidden() {
		return isHidden;
	}

	@Override
	public void close() {}
}
