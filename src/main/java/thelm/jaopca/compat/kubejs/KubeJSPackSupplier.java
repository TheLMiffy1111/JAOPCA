package thelm.jaopca.compat.kubejs;

import java.util.function.Consumer;

import dev.latvian.kubejs.KubeJSPaths;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IResourcePack;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "kubejs")
public class KubeJSPackSupplier implements IPackSupplier {

	@Override
	public void addPacks(Consumer<IResourcePack> resourcePacks) {
		resourcePacks.accept(new FolderPack(KubeJSPaths.DIRECTORY.toFile()));
	}
}
