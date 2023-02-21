package thelm.jaopca.api.modules;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;

public interface IModule extends Comparable<IModule> {

	String getName();

	default boolean isPassive() {
		return false;
	}

	default void defineModuleConfigPre(IModuleData moduleData, IDynamicSpecConfig config) {}

	default void defineMaterialConfigPre(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {}

	default Multimap<Integer, String> getModuleDependencies() {
		return ImmutableSetMultimap.of();
	}

	default List<IFormRequest> getFormRequests() {
		return Collections.emptyList();
	}

	default Set<MaterialType> getMaterialTypes() {
		return Collections.emptySet();
	}

	default Set<String> getDefaultMaterialBlacklist() {
		return Collections.emptyNavigableSet();
	}

	default void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {}

	default void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {}

	default void onMaterialComputeComplete(IModuleData moduleData) {}

	default void onInit(IModuleData moduleData, FMLInitializationEvent event) {}

	default void onPostInit(IModuleData moduleData, FMLPostInitializationEvent event) {}

	default void onLoadComplete(IModuleData moduleData, FMLLoadCompleteEvent event) {}

	@Override
	default int compareTo(IModule other) {
		return getName().compareTo(other.getName());
	}
}
