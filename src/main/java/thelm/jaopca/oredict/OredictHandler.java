package thelm.jaopca.oredict;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Splitter;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.config.ConfigHandler;
import thelm.jaopca.utils.MiscHelper;

public class OredictHandler {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String JAOPCA_OREDICT_MODULE = JAOPCAOredictModule.class.getCanonicalName();
	private static final TreeMap<String, IOredictModule> OREDICT_MODULES = new TreeMap<>();
	private static final TreeSet<String> OREDICT_NAMES = new TreeSet<>();
	private static boolean initialized = false;

	public static void initialize() {
		initialized = true;
		Arrays.stream(OreDictionary.getOreNames()).
		filter(name->!OreDictionary.getOres(name, false).isEmpty()).
		forEach(OREDICT_NAMES::add);
	}

	public static void onOreRegister(OreDictionary.OreRegisterEvent event) {
		if(!initialized) {
			initialize();
		}
		OREDICT_NAMES.add(event.Name);
	}

	public static Set<String> getOredict() {
		if(!initialized) {
			initialize();
		}
		return OREDICT_NAMES;
	}

	public static Collection<IOredictModule> getOredictModules() {
		return OREDICT_MODULES.values();
	}

	public static void findOredictModules(ASMDataTable asmDataTable) {
		OREDICT_MODULES.clear();
		Set<ASMData> annotationData = asmDataTable.getAll(JAOPCA_OREDICT_MODULE);
		Predicate<String> modVersionNotLoaded = MiscHelper.INSTANCE.modVersionNotLoaded(LOGGER);
		Predicate<String> classNotExists = MiscHelper.INSTANCE::classNotExists;
		for(ASMData aData : annotationData) {
			List<String> modDeps = (List<String>)aData.getAnnotationInfo().get("modDependencies");
			List<String> classDeps = (List<String>)aData.getAnnotationInfo().get("classDependencies");
			String className = aData.getClassName();
			if(modDeps != null && modDeps.stream().filter(Objects::nonNull).anyMatch(modVersionNotLoaded)) {
				LOGGER.info("Oredict module {} has missing mod dependencies, skipping", new Object[] {className});
				continue;
			}
			if(classDeps != null && classDeps.stream().filter(Objects::nonNull).anyMatch(classNotExists)) {
				LOGGER.info("Oredict module {} has missing class dependencies, skipping", new Object[] {className});
				continue;
			}
			try {
				Class<?> moduleClass = Class.forName(className);
				Class<? extends IOredictModule> moduleInstanceClass = moduleClass.asSubclass(IOredictModule.class);
				IOredictModule module;
				try {
					Method method = moduleClass.getMethod("getInstance");
					module = (IOredictModule)method.invoke(null);
				}
				catch(NoSuchMethodException | InvocationTargetException e) {
					module = moduleInstanceClass.newInstance();
				}
				if(ConfigHandler.OREDICT_MODULE_BLACKLIST.contains(module.getName())) {
					LOGGER.info("Oredict module {} is disabled in config, skipping", new Object[] {module.getName()});
				}
				if(OREDICT_MODULES.putIfAbsent(module.getName(), module) != null) {
					LOGGER.fatal("Oredict module name conflict: {} for {} and {}", new Object[] {module.getName(), OREDICT_MODULES.get(module.getName()).getClass(), module.getClass()});
					continue;
				}
				LOGGER.debug("Loaded oredict module {}", new Object[] {module.getName()});
			}
			catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LOGGER.fatal("Unable to load oredict module {}", new Object[] {className, e});
			}
		}
	}

	public static void register() {
		for(IOredictModule module : getOredictModules()) {
			module.register();
		}
		Splitter lineSplitter = Splitter.on(',').omitEmptyStrings().trimResults();
		Splitter oredictSplitter = Splitter.on('=').limit(2).trimResults();
		for(String line : ConfigHandler.CUSTOM_OREDICT) {
			for(String entry : lineSplitter.split(line)) {
				List<String> split = oredictSplitter.splitToList(entry);
				if(split.size() != 2) {
					LOGGER.warn("Custom oredict entry [{}] has no specified name", new Object[] {entry});
					continue;
				}
				ItemStack stack = MiscHelper.INSTANCE.parseMetaItem(split.get(0));
				if(stack == null || stack.getItem() == null) {
					LOGGER.warn("Custom oredict entry [{}] has empty item", new Object[] {entry});
					continue;
				}
				// Override the config blacklist here
				OreDictionary.registerOre(split.get(1), stack);
				LOGGER.info("Registered custom oredict entry [{}]", new Object[] {entry});
			}
		}
	}
}
