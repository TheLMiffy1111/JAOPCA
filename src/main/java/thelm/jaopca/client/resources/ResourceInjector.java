package thelm.jaopca.client.resources;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePack;

public class ResourceInjector {

	private static final Logger LOGGER = LogManager.getLogger();

	public static class PackFinder implements RepositorySource {

		public static final PackFinder INSTANCE = new PackFinder();

		@Override
		public void loadPacks(Consumer<Pack> packConsumer) {
			Pack packInfo = Pack.readMetaAndCreate("jaopca:inmemory", Component.literal("JAOPCA In Memory Resources"), true, packId->{
				InMemoryResourcePack pack = new InMemoryResourcePack(packId, true);
				ModuleHandler.onCreateResourcePack(pack);
				return pack;
			}, PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.BUILT_IN);
			packConsumer.accept(packInfo);
		}
	}
}
