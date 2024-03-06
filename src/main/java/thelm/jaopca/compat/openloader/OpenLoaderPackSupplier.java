package thelm.jaopca.compat.openloader;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.openloader.config.ConfigSchema;
import net.darkhax.openloader.packs.RepoType;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.neoforged.fml.loading.FMLPaths;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "openloader")
public class OpenLoaderPackSupplier implements IPackSupplier {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void addPacks(Consumer<PackResources> resourcePacks) {
		Path configDir = FMLPaths.CONFIGDIR.get().resolve("openloader");
		ConfigSchema config = ConfigSchema.load(configDir);
		if(config.dataPacks.enabled) {
			List<File> dirs = new ArrayList<>();
			dirs.add(configDir.resolve(RepoType.DATA.getPath()).toFile());
			for(String customDir : config.dataPacks.additionalFolders) {
				dirs.add(new File(customDir));
			}
			for(File loaderDir : dirs) {
				if(!loaderDir.isDirectory()) {
					LOGGER.error("Could not read from {} as it is not a directory.", loaderDir.getAbsolutePath());
				}
				else {
					try {
						File[] files = loaderDir.listFiles();
						if(files == null) {
							LOGGER.error("Could not read from {}.", loaderDir.getAbsolutePath());
						}
						else {
							for(File file : files) {
								boolean isPack = isArchivePack(file, false) || isFolderPack(file, false);
								if(isPack) {
									resourcePacks.accept(file.isDirectory() ? new PathPackResources(file.getName(), file.toPath(), false) : new FilePackResources(file.getName(), new FilePackResources.SharedZipFileAccess(file), false, ""));
								}
								else {
									isArchivePack(file, true);
									isFolderPack(file, true);
								}
							}
						}
					}
					catch(Exception e) {
						LOGGER.error("Could not read from {}.", loaderDir.getAbsolutePath(), e);
					}
				}
			}
		}
	}

	private static boolean isArchivePack(File candidate, boolean logIssues) {
		if(candidate.isFile()) {
			String fileName = candidate.getName();
			boolean valid = endsWithIgnoreCase(fileName, ".zip") || endsWithIgnoreCase(fileName, ".jar");
			if(!valid && logIssues) {
				LOGGER.warn("Can not load {} as an archive pack as it is not a .zip or .jar file.", candidate.getAbsolutePath());
			}
			return valid;
		}
		if(logIssues) {
			LOGGER.warn("Can not load {} as an archive pack as it is not a file.", candidate.getAbsolutePath());
		}
		return false;
	}

	private static boolean isFolderPack(File candidate, boolean logIssues) {
		if(candidate.isDirectory()) {
			boolean valid = new File(candidate, "pack.mcmeta").isFile();
			if(!valid && logIssues) {
				LOGGER.warn("Can not load {} as a folder pack as it is missing a pack.mcmeta file.", candidate.getAbsolutePath());
			}
			return valid;
		}
		if(logIssues) {
			LOGGER.warn("Can not load {} as a folder pack as it is not a directory.", candidate.getAbsolutePath());
		}
		return false;
	}

	private static boolean endsWithIgnoreCase(String str, String suffix) {
		int suffixLength = suffix.length();
		return str.regionMatches(true, str.length()-suffixLength, suffix, 0, suffixLength);
	}
}
