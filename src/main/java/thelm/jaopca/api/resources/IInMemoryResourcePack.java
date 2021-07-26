package thelm.jaopca.api.resources;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

public interface IInMemoryResourcePack extends PackResources {

	IInMemoryResourcePack putInputStream(PackType type, ResourceLocation location, Supplier<? extends InputStream> streamSupplier);

	IInMemoryResourcePack putInputStreams(PackType type, Map<ResourceLocation, Supplier<? extends InputStream>> map);

	IInMemoryResourcePack putByteArray(PackType type, ResourceLocation location, byte[] file);

	IInMemoryResourcePack putByteArrays(PackType type, Map<ResourceLocation, byte[]> map);

	IInMemoryResourcePack putString(PackType type, ResourceLocation location, String str);

	IInMemoryResourcePack putStrings(PackType type, Map<ResourceLocation, String> map);

	IInMemoryResourcePack putJson(PackType type, ResourceLocation location, JsonElement json);

	IInMemoryResourcePack putJsons(PackType type, Map<ResourceLocation, ? extends JsonElement> map);
}
