package thelm.jaopca.modules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.utils.MiscHelper;

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
		Predicate<String> modVersionNotLoaded = MiscHelper.INSTANCE.modVersionNotLoaded(LOGGER);
		Predicate<String> classNotExists = MiscHelper.INSTANCE::classNotExists;
		for(ASMData aData : annotationData) {
			List<String> modDeps = (List<String>)aData.getAnnotationInfo().get("modDependencies");
			List<String> classDeps = (List<String>)aData.getAnnotationInfo().get("classDependencies");
			String className = aData.getClassName();
			if(modDeps != null && modDeps.stream().filter(Objects::nonNull).anyMatch(modVersionNotLoaded)) {
				LOGGER.info("Module {} has missing mod dependencies, skipping", new Object[] {className});
				continue;
			}
			if(classDeps != null && classDeps.stream().filter(Objects::nonNull).anyMatch(classNotExists)) {
				LOGGER.info("Module {} has missing class dependencies, skipping", new Object[] {className});
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
					LOGGER.fatal("Module name conflict: {} for {} and {}", new Object[] {module.getName(), MODULES.get(module.getName()).getClass(), module.getClass()});
					continue;
				}
				ModuleData mData = new ModuleData(module);
				MODULE_DATAS.put(module, mData);
				LOGGER.debug("Loaded module {}", new Object[] {module.getName()});
			}
			catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.fatal("Unable to load module {}", new Object[] {className, e});
			}
		}
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

	public static void onLoadComplete(FMLLoadCompleteEvent event) {
		for(IModule module : getModules()) {
			module.onLoadComplete(getModuleData(module), event);
		}
	}
}
