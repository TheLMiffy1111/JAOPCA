package thelm.jaopca.compat.kubejs;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

import dev.latvian.mods.kubejs.KubeJSPaths;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "kubejs")
public class KubeJSPackSupplier implements IPackSupplier {

	@Override
	public void addPacks(Consumer<PackResources> resourcePacks) {
		PackLocationInfo emptyLocation = new PackLocationInfo("", Component.empty(), PackSource.DEFAULT, Optional.empty());
		resourcePacks.accept(new PathPackResources(emptyLocation, KubeJSPaths.DIRECTORY));
		for(File file : KubeJSPaths.DATA.toFile().listFiles()) {
			if(file.isFile() && file.getName().endsWith(".zip")) {
				resourcePacks.accept(new FilePackResources(emptyLocation, new FilePackResources.SharedZipFileAccess(file), ""));
			}
		}
	}
}
