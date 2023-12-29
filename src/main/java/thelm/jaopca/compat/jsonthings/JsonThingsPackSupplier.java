package thelm.jaopca.compat.jsonthings;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.fml.loading.FMLPaths;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "jsonthings")
public class JsonThingsPackSupplier implements IPackSupplier {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void addPacks(Consumer<PackResources> resourcePacks) {
		Path thingpacks = FMLPaths.GAMEDIR.get().resolve("thingpacks");
		try(DirectoryStream<Path> directorystream = Files.newDirectoryStream(thingpacks)) {
			for(Path path : directorystream) {
				Pack.ResourcesSupplier supplier = FolderRepositorySource.detectPackResources(path, false);
				if(supplier != null) {
					resourcePacks.accept(supplier.open(path.getFileName().toString()));
				}
			}
		}
		catch(IOException e) {
			LOGGER.error("Could not read from {}.", thingpacks, e);
		}
	}
}
