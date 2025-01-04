package thelm.jaopca.compat.openloader;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.common.api.service.Services;
import net.darkhax.openloader.common.impl.OpenLoader;
import net.darkhax.openloader.common.impl.conig.Config;
import net.darkhax.openloader.common.impl.packs.PackContentType;
import net.darkhax.openloader.common.impl.packs.PackFileType;
import net.darkhax.pricklemc.common.api.config.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "openloader")
public class OpenLoaderPackSupplier implements IPackSupplier {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void addPacks(Consumer<PackResources> resourcePacks) {
		Config config = ConfigManager.load("openloader/options", new Config());
		if(config.load_data_packs) {
			List<File> scanLocations = new ArrayList<>();
			scanLocations.add(new File(Services.PLATFORM.getConfigDirectory(), "openloader/packs"));
			for(String location : config.additional_locations) {
				if(isValidPath(location)) {
					scanLocations.add(new File(location));
				}
			}
			for(File location : scanLocations) {
				loadFrom(location, resourcePacks);
			}
		}
	}

	private void loadFrom(File location, Consumer<PackResources> resourcePacks) {
		PackFileType type = PackFileType.from(location);
		if(!type.isLoadable()) {
			if(location.isDirectory() && location.exists()) {
				for(File subLocation : Objects.requireNonNull(location.listFiles())) {
					if(!OpenLoader.INVALID_FOLDERS.contains(subLocation.getName())) {
						loadFrom(subLocation, resourcePacks);
					}
				}
			}
		}
		else {
			PackContentType contentType = PackContentType.from(location.toPath());
			if(contentType.isFor(PackType.SERVER_DATA)) {
				PackLocationInfo locationInfo = new PackLocationInfo("openloader/"+location.getAbsolutePath(), Component.empty(), PackSource.DEFAULT, Optional.empty());
				resourcePacks.accept(type.createPackSupplier(location).openPrimary(locationInfo));
			}
		}
	}

	private static boolean isValidPath(String path) {
		try {
			Paths.get(path);
		}
		catch(InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}
}
