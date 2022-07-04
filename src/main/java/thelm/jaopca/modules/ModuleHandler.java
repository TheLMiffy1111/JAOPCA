package thelm.jaopca.modules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicates;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.materials.MaterialHandler;

public class ModuleHandler {

	private ModuleHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String JAOPCA_MODULE = JAOPCAModule.class.getCanonicalName();
	private static final TreeMap<String, IModule> MODULES = new TreeMap<>();
	private static final TreeMap<IModule, ModuleData> MODULE_DATAS = new TreeMap<>();

	public static Map<String, IModule> getModuleMap() {
		return MODULES;
	}

	public static Collection<IModule> getModules() {
		return MODULES.values();
	}

	public static Map<IModule, ModuleData> getModuleDataMap() {
		return MODULE_DATAS;
	}

	public static ModuleData getModuleData(String name) {
		IModule module = MODULES.get(name);
		return module != null ? MODULE_DATAS.get(module) : null;
	}

	public static ModuleData getModuleData(IModule module) {
		return MODULE_DATAS.get(module);
	}

	public static Collection<ModuleData> getModuleDatas() {
		return MODULE_DATAS.values();
	}

	public static void findModules(ASMDataTable asmDataTable) {
		MODULES.clear();
		Set<ASMData> annotationData = asmDataTable.getAll(JAOPCA_MODULE);
		for(ASMData aData : annotationData) {
			List<String> deps = (List<String>)aData.getAnnotationInfo().get("modDependencies");
			String className = aData.getClassName();
			if(deps != null && deps.stream().filter(Predicates.notNull()).anyMatch(ModuleHandler::isModVersionNotLoaded)) {
				LOGGER.info("Module {} has missing mod dependencies, skipping", className);
				continue;
			}
			try {
				Class<?> moduleClass = Class.forName(className);
				Class<? extends IModule> moduleInstanceClass = moduleClass.asSubclass(IModule.class);
				IModule module;
				try {
					Method method = moduleClass.getMethod("getInstance");
					module = (IModule)method.invoke(null);
				}
				catch(NoSuchMethodException | InvocationTargetException e) {
					module = moduleInstanceClass.newInstance();
				}
				if(MODULES.putIfAbsent(module.getName(), module) != null) {
					LOGGER.fatal("Module name conflict: {} for {} and {}", module.getName(), MODULES.get(module.getName()).getClass(), module.getClass());
					continue;
				}
				ModuleData mData = new ModuleData(module);
				MODULE_DATAS.put(module, mData);
				LOGGER.debug("Loaded module {}", module.getName());
			}
			catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.fatal("Unable to load module {}", className, e);
			}
		}
	}

	static boolean isModVersionNotLoaded(String dep) {
		Loader modLoader = Loader.instance();
		int separatorIndex = dep.lastIndexOf('@');
		String modId = dep.substring(0, separatorIndex == -1 ? dep.length() : separatorIndex);
		String spec = separatorIndex == -1 ? "0" : dep.substring(separatorIndex+1);
		VersionRange versionRange;
		try {
			versionRange = VersionRange.createFromVersionSpec(spec);
		}
		catch(InvalidVersionSpecificationException e) {
			LOGGER.warn("Unable to parse version spec {} for mod id {}", spec, modId, e);
			return true;
		}
		if(Loader.isModLoaded(modId)) {
			ArtifactVersion version = modLoader.getIndexedModList().get(modId).getProcessedVersion();
			if(versionRange.containsVersion(version)) {
				return false;
			}
			else {
				LOGGER.warn("Mod {} in version range {} was requested, was {}", modId, versionRange, version);
				return true;
			}
		}
		return true;
	}

	public static void computeValidMaterials() {
		for(ModuleData data : getModuleDatas()) {
			List<IMaterial> materials = MaterialHandler.getMaterials().stream().
					filter(data.getModule().isPassive() ?
							material->data.getConfigPassiveMaterialWhitelist().contains(material.getName()) :
								data::isMaterialModuleValid).
					collect(Collectors.toList());
			for(IMaterial material : materials) {
				if(data.isMaterialDependencyValid(material, new HashSet<>())) {
					data.addDependencyRequestedMaterial(material);
				}
				else {
					data.addRejectedMaterial(material);
				}
			}
		}
		for(ModuleData data : getModuleDatas()) {
			List<IMaterial> materials = MaterialHandler.getMaterials().stream().filter(data::isMaterialValid).collect(Collectors.toList());
			data.setMaterials(materials);
		}
	}

	public static void onMaterialComputeComplete() {
		for(IModule module : getModules()) {
			module.onMaterialComputeComplete(getModuleData(module));
		}
	}

	public static void onInit(FMLInitializationEvent event) {
		for(IModule module : getModules()) {
			module.onInit(getModuleData(module), event);
		}
	}

	public static void onPostInit(FMLPostInitializationEvent event) {
		for(IModule module : getModules()) {
			module.onPostInit(getModuleData(module), event);
		}
	}
}
