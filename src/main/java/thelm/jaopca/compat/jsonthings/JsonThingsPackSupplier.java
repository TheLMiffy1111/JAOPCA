package thelm.jaopca.compat.jsonthings;

import java.io.File;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResourcePack;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "jsonthings")
public class JsonThingsPackSupplier implements IPackSupplier {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void addPacks(Consumer<IResourcePack> resourcePacks) {
		File thingpacks = ThingResourceManager.instance().getThingPacksLocation();
		File[] files = thingpacks.listFiles();
		if(files == null) {
			LOGGER.error("Could not read from {}.", thingpacks.getAbsolutePath());
		}
		else {
			for(File file : files) {
				resourcePacks.accept(file.isDirectory() ? new FolderPack(file) : new FilePack(file));
			}
		}
	}
}
