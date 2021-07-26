package thelm.jaopca.client.resources;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		public void loadPacks(Consumer<Pack> packConsumer, Pack.PackConstructor packConstructor) {
			Pack packInfo = Pack.create("inmemory:jaopca", true, ()->{
				InMemoryResourcePack pack = new InMemoryResourcePack("inmemory:jaopca", true);
				ModuleHandler.onCreateResourcePack(pack);
				return pack;
			}, packConstructor, Pack.Position.BOTTOM, PackSource.BUILT_IN);
			packConsumer.accept(packInfo);
		}
	}
}
