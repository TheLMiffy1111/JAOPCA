package thelm.jaopca.compat.kubejs;

import java.io.File;
import java.util.function.Consumer;

import dev.latvian.mods.kubejs.KubeJSPaths;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "kubejs")
public class KubeJSPackSupplier implements IPackSupplier {

	@Override
	public void addPacks(Consumer<PackResources> resourcePacks) {
		resourcePacks.accept(new PathPackResources("kubejs", KubeJSPaths.DIRECTORY, false));
		for(File file : KubeJSPaths.DATA.toFile().listFiles()) {
			if(file.isFile() && file.getName().endsWith(".zip")) {
				String name = "kubejs/"+file.getName();
				resourcePacks.accept(new FilePackResources(name, file, false));
			}
		}
	}
}
