package thelm.jaopca.resources;

import java.io.ByteArrayInputStream;
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

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.InclusiveRange;
import thelm.jaopca.api.resources.IInMemoryResourcePack;

public class InMemoryResourcePack implements IInMemoryResourcePack {

	private static final Gson GSON = new GsonBuilder().create();
	private final PackLocationInfo packLocation;
	private final boolean isHidden;
	private final ResourceMetadata metadata = ResourceMetadata.of(
			PackMetadataSection.CLIENT_TYPE, new PackMetadataSection(Component.literal("JAOPCA In-Memory Resources"),
					new InclusiveRange<>(PackFormat.of(PackFormat.lastPreMinorVersion(PackType.CLIENT_RESOURCES)))),
			PackMetadataSection.SERVER_TYPE, new PackMetadataSection(Component.literal("JAOPCA In-Memory Data"),
					new InclusiveRange<>(PackFormat.of(PackFormat.lastPreMinorVersion(PackType.SERVER_DATA)))));
	private final TreeMap<String, Supplier<? extends InputStream>> files = new TreeMap<>();

	public InMemoryResourcePack(PackLocationInfo packLocation, boolean isHidden) {
		this.packLocation = packLocation;
		this.isHidden = isHidden;
	}

	private static String getPath(PackType packType, Identifier location) {
		return String.format(Locale.ROOT, "%s/%s/%s", packType.getDirectory(), location.getNamespace(), location.getPath());
	}

	@Override
	public IInMemoryResourcePack putInputStream(PackType type, Identifier location, Supplier<? extends InputStream> streamSupplier) {
		files.put(getPath(type, location), streamSupplier);
		return this;
	}

	@Override
	public IInMemoryResourcePack putInputStreams(PackType type, Map<Identifier, Supplier<? extends InputStream>> map) {
		map.forEach((location, streamSupplier)->files.put(getPath(type, location), streamSupplier));
		return this;
	}

	@Override
	public IInMemoryResourcePack putByteArray(PackType type, Identifier location, byte[] file) {
		return putInputStream(type, location, ()->new ByteArrayInputStream(file));
	}

	@Override
	public IInMemoryResourcePack putByteArrays(PackType type, Map<Identifier, byte[]> map) {
		return putInputStreams(type, Maps.transformValues(map, file->()->new ByteArrayInputStream(file)));
	}

	@Override
	public IInMemoryResourcePack putString(PackType type, Identifier location, String str) {
		return putByteArray(type, location, str.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public IInMemoryResourcePack putStrings(PackType type, Map<Identifier, String> map) {
		return putByteArrays(type, Maps.transformValues(map, str->str.getBytes(StandardCharsets.UTF_8)));
	}

	@Override
	public IInMemoryResourcePack putJson(PackType type, Identifier location, JsonElement json) {
		return putString(type, location, GSON.toJson(json));
	}

	@Override
	public IInMemoryResourcePack putJsons(PackType type, Map<Identifier, ? extends JsonElement> map) {
		return putStrings(type, Maps.transformValues(map, json->GSON.toJson(json)));
	}

	@Override
	public IoSupplier<InputStream> getRootResource(String... path) {
		String filePath = String.join("/", path);
		return files.containsKey(filePath) ? ()->files.get(filePath).get() : null;
	}

	@Override
	public IoSupplier<InputStream> getResource(PackType type, Identifier location) {
		String filePath = getPath(type, location);
		return files.containsKey(filePath) ? ()->files.get(filePath).get() : null;
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput resourceOutput) {
		String prefix = type.getDirectory()+'/'+namespace+'/';
		String prefix1 = prefix+path+'/';
		files.forEach((filePath, streamSupplier)->{
			if(filePath.startsWith(prefix1)) {
				resourceOutput.accept(Identifier.fromNamespaceAndPath(namespace, filePath.substring(prefix.length())), streamSupplier::get);
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
	public <T> T getMetadataSection(MetadataSectionType<T> type) throws IOException {
		return metadata.getSection(type).orElse(null);
	}

	@Override
	public PackLocationInfo location() {
		return packLocation;
	}

	@Override
	public boolean isHidden() {
		return isHidden;
	}

	@Override
	public void close() {}
}
