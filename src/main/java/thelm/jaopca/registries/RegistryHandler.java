package thelm.jaopca.registries;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryHandler {

	private RegistryHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final ArrayListMultimap<Class<?>, IForgeRegistryEntry<?>> REGISTRY_ENTRIES = ArrayListMultimap.create();

	public static void registerForgeRegistryEntry(IForgeRegistryEntry<?> entry) {
		Objects.requireNonNull(entry);
		REGISTRY_ENTRIES.put(entry.getRegistryType(), entry);
	}

	public static void onRegister(RegistryEvent.Register<?> event) {
		@SuppressWarnings("rawtypes")
		IForgeRegistry registry = event.getRegistry();
		Class<?> type = registry.getRegistrySuperType();
		if(REGISTRY_ENTRIES.containsKey(type)) {
			for(IForgeRegistryEntry<?> entry : REGISTRY_ENTRIES.get(type)) {
				registry.register(entry);
				LOGGER.debug("Registered {} to registry {}", entry.getRegistryName(), registry.getRegistryName());
			}
			REGISTRY_ENTRIES.removeAll(type);
		}
	}
}
