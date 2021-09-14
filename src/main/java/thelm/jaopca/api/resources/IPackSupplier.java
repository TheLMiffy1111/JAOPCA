package thelm.jaopca.api.resources;

import java.util.function.Consumer;

import net.minecraft.resources.IResourcePack;

public interface IPackSupplier {

	void addPacks(Consumer<IResourcePack> resourcePacks);
}
