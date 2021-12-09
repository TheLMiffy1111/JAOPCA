package thelm.jaopca.compat.kubejs;

import java.util.function.Consumer;

import net.minecraft.resources.IResourcePack;
import thelm.jaopca.api.resources.IPackSupplier;
import thelm.jaopca.api.resources.JAOPCAPackSupplier;

@JAOPCAPackSupplier(modDependencies = "kubejs")
public class KubeJSPackSupplier implements IPackSupplier {

	@Override
	public void addPacks(Consumer<IResourcePack> resourcePacks) {
		resourcePacks.accept(new KubeJSFolderPack());
	}
}
