package thelm.jaopca.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.Registry;
import net.minecraft.server.ReloadableServerResources;

@Mixin(ReloadableServerResources.class)
public interface ReloadableServerResourcesAccessor {

	@Accessor("postponedTags")
	List<Registry.PendingTags<?>> postponedTags();
}
