package thelm.jaopca.modules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.objectweb.asm.Type;

import com.google.common.base.Predicates;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.api.resources.IInMemoryResourcePack;
import thelm.jaopca.materials.MaterialHandler;

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
				filter(data->JAOPCA_MODULE.equals(data.annotationType())).
				collect(Collectors.toList());
		for(AnnotationData aData : annotationData) {
			List<String> deps = (List<String>)aData.annotationData().get("modDependencies");
			String className = aData.clazz().getClassName();
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
					//throw new IllegalStateException(String.format("Module name conflict: %s for %s and %s", module.getName(), MODULES.get(module.getName()).getClass(), module.getClass()));
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
		ModList modList = ModList.get();
		int separatorIndex = dep.lastIndexOf('@');
		String modId = dep.substring(0, separatorIndex == -1 ? dep.length() : separatorIndex);
		String spec = separatorIndex == -1 ? "0" : dep.substring(separatorIndex+1, dep.length());
		VersionRange versionRange;
		try {
			versionRange = VersionRange.createFromVersionSpec(spec);
		}
		catch(InvalidVersionSpecificationException e) {
			LOGGER.warn("Unable to parse version spec {} for mod id {}", spec, modId, e);
			return true;
		}
		if(modList.isLoaded(modId)) {
			ArtifactVersion version = modList.getModContainerById(modId).get().getModInfo().getVersion();
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
