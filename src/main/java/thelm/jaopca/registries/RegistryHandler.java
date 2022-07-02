package thelm.jaopca.registries;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.utils.MiscHelper;

public class RegistryHandler {

	private RegistryHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final TreeMap<String, String> LEGACY_REMAPS = new TreeMap<>();
	private static boolean initializedRemaps = false;

	public static void initializeRemaps() {
		initializedRemaps = true;
		for(IModule module : ModuleHandler.getModules()) {
			LEGACY_REMAPS.putAll(module.getLegacyRemaps());
		}
	}

	public static <T extends IForgeRegistryEntry<T>> void onMissingMappings(RegistryEvent.MissingMappings<T> event) {
		if(!initializedRemaps) {
			initializeRemaps();
		}
		m:for(RegistryEvent.MissingMappings.Mapping<T> mapping : event.getMappings()) {
			String[] names = mapping.key.getPath().split("_", 2);
			if(names.length == 2) {
				for(Map.Entry<String, String> remap : LEGACY_REMAPS.entrySet()) {
					if(names[1].startsWith(remap.getKey())) {
						String materialName = names[1].substring(remap.getKey().length());
						Optional<? extends IMaterial> material = MaterialHandler.getMaterials().stream().
								filter(m->m.getName().equalsIgnoreCase(materialName)).findAny();
						if(material.isPresent()) {
							String path = remap.getValue()+'_'+MiscHelper.INSTANCE.toLowercaseUnderscore(material.get().getName());
							ResourceLocation remapLocation = new ResourceLocation(mapping.key.getNamespace(), path);
							IForgeRegistry<T> reg = RegistryManager.ACTIVE.getRegistry(event.getName());
							if(reg.containsKey(remapLocation)) {
								mapping.remap(reg.getValue(remapLocation));
								continue m;
							}
						}
					}
				}
			}
		}
	}
}
