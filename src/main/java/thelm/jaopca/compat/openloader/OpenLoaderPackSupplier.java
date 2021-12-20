package thelm.jaopca.compat.openloader;

import java.io.File;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.FilePack;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResourcePack;
import net.minecraftforge.fml.loading.FMLPaths;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "openloader")
public class OpenLoaderPackSupplier implements IPackSupplier {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void addPacks(Consumer<IResourcePack> resourcePacks) {
		File loaderDir = FMLPaths.GAMEDIR.get().resolve("openloader/data").toFile();
		if(!loaderDir.isDirectory()) {
			LOGGER.error("Could not read from {} as it's not a directory.", loaderDir);
		}
		else {
			try {
				File[] files = loaderDir.listFiles();
				if(files == null) {
					LOGGER.error("Could not read from {}.", loaderDir);
				}
				else {
					for(File file : files) {
						boolean isPack = file.isFile() && file.getName().endsWith(".zip") ||
								file.isDirectory() && new File(file, "pack.mcmeta").isFile();
						if(isPack) {
							resourcePacks.accept(file.isDirectory() ? new FolderPack(file) : new FilePack(file));
						}
					}
				}
			}
			catch(Exception e) {
				LOGGER.error("Could not read from {}.", loaderDir, e);
			}
		}
	}
}
