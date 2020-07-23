package thelm.jaopca.client.resources;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.resources.InMemoryResourcePack;

public class ResourceInjector {

	private static final Logger LOGGER = LogManager.getLogger();

	public static class PackFinder implements IPackFinder {

		public static final PackFinder INSTANCE = new PackFinder();

		@Override
		public <T extends ResourcePackInfo> void func_230230_a_(Consumer<T> packList, IFactory<T> factory) {
			T packInfo = ResourcePackInfo.createResourcePack("inmemory:jaopca", true, ()->{
				InMemoryResourcePack pack = new InMemoryResourcePack("inmemory:jaopca", true);
				ModuleHandler.onCreateResourcePack(pack);
				return pack;
			}, factory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.field_232626_b_);
			packList.accept(packInfo);
		}
	}
}
