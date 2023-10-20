package thelm.jaopca.compat.kubejs;

import java.io.File;
import java.util.function.Consumer;

import dev.latvian.mods.kubejs.KubeJSPaths;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "kubejs")
public class KubeJSPackSupplier implements IPackSupplier {

	@Override
	public void addPacks(Consumer<PackResources> resourcePacks) {
		resourcePacks.accept(new FolderPackResources(KubeJSPaths.DIRECTORY.toFile()));
		for(File file : KubeJSPaths.DATA.toFile().listFiles()) {
			if(file.isFile() && file.getName().endsWith(".zip")) {
				resourcePacks.accept(new FilePackResources(file));
			}
		}
	}
}
