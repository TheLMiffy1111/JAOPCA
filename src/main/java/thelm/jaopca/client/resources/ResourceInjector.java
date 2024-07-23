package thelm.jaopca.client.resources;

import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
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
			PackLocationInfo packLocation = new PackLocationInfo("jaopca:inmemory", Component.literal("JAOPCA In Memory Resources"), PackSource.BUILT_IN, Optional.empty());
			PackSelectionConfig packSelection = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
			Pack packInfo = Pack.readMetaAndCreate(packLocation, BuiltInPackSource.fromName(packId->{
				InMemoryResourcePack pack = new InMemoryResourcePack(packId, true);
				ModuleHandler.onCreateResourcePack(pack);
				return pack;
			}), PackType.SERVER_DATA, packSelection);
			packConsumer.accept(packInfo);
		}
	}
}
