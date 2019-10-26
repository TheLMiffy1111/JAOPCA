package thelm.jaopca.api.resources;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

public interface IInMemoryResourcePack extends IResourcePack {

	IInMemoryResourcePack putInputStream(ResourcePackType type, ResourceLocation location, Supplier<? extends InputStream> streamSupplier);

	IInMemoryResourcePack putInputStreams(ResourcePackType type, Map<ResourceLocation, Supplier<? extends InputStream>> map);

	IInMemoryResourcePack putByteArray(ResourcePackType type, ResourceLocation location, byte[] file);

	IInMemoryResourcePack putByteArrays(ResourcePackType type, Map<ResourceLocation, byte[]> map);

	IInMemoryResourcePack putString(ResourcePackType type, ResourceLocation location, String str);

	IInMemoryResourcePack putStrings(ResourcePackType type, Map<ResourceLocation, String> map);

	IInMemoryResourcePack putJson(ResourcePackType type, ResourceLocation location, JsonElement json);

	IInMemoryResourcePack putJsons(ResourcePackType type, Map<ResourceLocation, ? extends JsonElement> map);
}
