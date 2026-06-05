package thelm.jaopca.api.resources;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.JsonElement;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

public interface IInMemoryResourcePack extends PackResources {

	IInMemoryResourcePack putInputStream(PackType type, Identifier location, Supplier<? extends InputStream> streamSupplier);

	IInMemoryResourcePack putInputStreams(PackType type, Map<Identifier, Supplier<? extends InputStream>> map);

	IInMemoryResourcePack putByteArray(PackType type, Identifier location, byte[] file);

	IInMemoryResourcePack putByteArrays(PackType type, Map<Identifier, byte[]> map);

	IInMemoryResourcePack putString(PackType type, Identifier location, String str);

	IInMemoryResourcePack putStrings(PackType type, Map<Identifier, String> map);

	IInMemoryResourcePack putJson(PackType type, Identifier location, JsonElement json);

	IInMemoryResourcePack putJsons(PackType type, Map<Identifier, ? extends JsonElement> map);
}
