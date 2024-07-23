package thelm.jaopca.modules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import com.google.common.base.Predicates;

import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforgespi.language.ModFileScanData.AnnotationData;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.api.resources.IInMemoryResourcePack;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.utils.MiscHelper;

public class ModuleHandler {

	private ModuleHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Type JAOPCA_MODULE = Type.getType(JAOPCAModule.class);
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
		return MODULE_DATAS.get(MODULES.get(name));
	}

	public static ModuleData getModuleData(IModule module) {
		return MODULE_DATAS.get(module);
	}

	public static Collection<ModuleData> getModuleDatas() {
		return MODULE_DATAS.values();
	}

	public static void findModules() {
		MODULES.clear();
		List<AnnotationData> annotationData = ModList.get().getAllScanData().stream().
				flatMap(data->data.getAnnotations().stream()).
				filter(data->JAOPCA_MODULE.equals(data.annotationType())).toList();
		Predicate<String> modVersionNotLoaded = MiscHelper.INSTANCE.modVersionNotLoaded(LOGGER);
		Predicate<String> classNotExists = MiscHelper.INSTANCE::classNotExists;
		for(AnnotationData aData : annotationData) {
			List<String> modDeps = (List<String>)aData.annotationData().get("modDependencies");
			List<String> classDeps = (List<String>)aData.annotationData().get("classDependencies");
			String className = aData.clazz().getClassName();
			if(modDeps != null && modDeps.stream().filter(Predicates.notNull()).anyMatch(modVersionNotLoaded)) {
				LOGGER.info("Module {} has missing mod dependencies, skipping", className);
				continue;
			}
			if(classDeps != null && classDeps.stream().filter(Predicates.notNull()).anyMatch(classNotExists)) {
				LOGGER.info("Module {} has missing class dependencies, skipping", className);
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

	public static void onCommonSetup(FMLCommonSetupEvent event) {
		for(IModule module : getModules()) {
			module.onCommonSetup(getModuleData(module), event);
		}
	}

	public static void onClientSetup(FMLClientSetupEvent event) {
		for(IModule module : getModules()) {
			module.onClientSetup(getModuleData(module), event);
		}
	}

	public static void onInterModEnqueue(InterModEnqueueEvent event) {
		for(IModule module : getModules()) {
			module.onInterModEnqueue(getModuleData(module), event);
		}
	}

	public static void onCreateResourcePack(IInMemoryResourcePack resourcePack) {
		for(IModule module : getModules()) {
			module.onCreateResourcePack(getModuleData(module), resourcePack);
		}
	}

	public static void onCreateDataPack(IInMemoryResourcePack resourcePack) {
		for(IModule module : getModules()) {
			module.onCreateDataPack(getModuleData(module), resourcePack);
		}
	}
}
