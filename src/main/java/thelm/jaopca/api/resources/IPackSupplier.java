package thelm.jaopca.api.resources;

import java.util.function.Consumer;

import net.minecraft.server.packs.PackResources;

public interface IPackSupplier {

	void addPacks(Consumer<PackResources> resourcePacks);
}
