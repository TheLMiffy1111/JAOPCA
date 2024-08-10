package thelm.jaopca.compat.jsonthings;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.gigaherz.jsonthings.things.parsers.ThingResourceManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.neoforged.fml.loading.FMLPaths;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "jsonthings")
public class JsonThingsPackSupplier implements IPackSupplier {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void addPacks(Consumer<PackResources> resourcePacks) {
		Path thingpacks = ThingResourceManager.instance().getThingPacksLocation();
		try(DirectoryStream<Path> directorystream = Files.newDirectoryStream(thingpacks)) {
			FolderRepositorySource.discoverPacks(
					thingpacks,
					LevelStorageSource.parseValidator(FMLPaths.GAMEDIR.get().resolve("allowed_symlinks.txt")),
					(path, supplier)->{
						String name = "jsonthings/"+path.getFileName().toString();
						PackLocationInfo location = new PackLocationInfo(name, Component.empty(), PackSource.DEFAULT, Optional.empty());
						resourcePacks.accept(supplier.openPrimary(location));
					});
		}
		catch(Exception e) {
			LOGGER.error("Could not read from {}.", thingpacks, e);
		}
	}
}
