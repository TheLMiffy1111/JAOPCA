package thelm.jaopca.config;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.materials.Material;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleData;
import thelm.jaopca.modules.ModuleHandler;

public class ConfigHandler {

	private ConfigHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static File configDir;
	private static File materialConfigDir;
	private static File moduleConfigDir;
	private static IDynamicSpecConfig mainConfig;
	private static final TreeMap<IMaterial, IDynamicSpecConfig> MATERIAL_CONFIGS = new TreeMap<>();
	private static final TreeMap<IModule, IDynamicSpecConfig> MODULE_CONFIGS = new TreeMap<>();

	public static boolean ingot = true;
	public static boolean gem = true;
	public static boolean crystal = true;
	public static boolean dust = true;
	public static boolean ingotPlain = true;
	public static boolean gemPlain = true;
	public static boolean crystalPlain = true;
	public static boolean dustPlain = true;

	private static final List<String> DEFAULT_GEM_OVERRIDES = Lists.newArrayList("diamond", "emerald", "lapis", "prismarine", "quartz");
	private static final List<String> DEFAULT_CRYSTAL_OVERRIDES = Lists.newArrayList();
	private static final List<String> DEFAULT_DUST_OVERRIDES = Lists.newArrayList("glowstone", "redstone");
	public static final Set<String> GEM_OVERRIDES = new TreeSet<>();
	public static final Set<String> CRYSTAL_OVERRIDES = new TreeSet<>();
	public static final Set<String> DUST_OVERRIDES = new TreeSet<>();

	public static double gammaValue = 2;

	public static void setupMainConfig(File configDir) {
		ConfigHandler.configDir = configDir;
		if(!configDir.exists() || !configDir.isDirectory()) {
			try {
				if(configDir.exists() && !configDir.isDirectory()) {
					LOGGER.warn("Config directory {} is a file, deleting", materialConfigDir);
					configDir.delete();
				}
				if(!configDir.mkdir()) {
					throw new Error("Could not create config directory "+configDir);
				}
			}
			catch(SecurityException e) {
				throw new Error("Could not create config directory "+configDir, e);
			}
		}

		mainConfig = new DynamicSpecConfig(CommentedFileConfig.builder(new File(configDir, "main.toml")).sync().autosave().build());

		mainConfig.setComment("materials", "Configurations related to materials.");
		ingot = mainConfig.getDefinedBoolean("materials.ingot", ingot, "Should the mod find ingot materials with ores.");
		gem = mainConfig.getDefinedBoolean("materials.gem", gem, "Should the mod find gem materials with ores.");
		crystal = mainConfig.getDefinedBoolean("materials.crystal", crystal, "Should the mod find crystal materials with ores.");
		dust = mainConfig.getDefinedBoolean("materials.dust", dust, "Should the mod find dust materials with ores.");
		ingotPlain = mainConfig.getDefinedBoolean("materials.ingotPlain", ingotPlain, "Should the mod find ingot materials without ores.");
		gemPlain = mainConfig.getDefinedBoolean("materials.gemPlain", gemPlain, "Should the mod find gem materials without ores.");
		crystalPlain = mainConfig.getDefinedBoolean("materials.crystalPlain", crystalPlain, "Should the mod find crystal materials without ores.");
		dustPlain = mainConfig.getDefinedBoolean("materials.dustPlain", dustPlain, "Should the mod find dust materials without ores.");

		mainConfig.setComment("materialOverrides", "Configurations related to material overrides.");
		GEM_OVERRIDES.addAll(mainConfig.getDefinedStringList("materialOverrides.gem", DEFAULT_GEM_OVERRIDES, "List of materials that should be gems."));
		CRYSTAL_OVERRIDES.addAll(mainConfig.getDefinedStringList("materialOverrides.crystal", DEFAULT_CRYSTAL_OVERRIDES, "List of materials that should be crystals."));
		DUST_OVERRIDES.addAll(mainConfig.getDefinedStringList("materialOverrides.dust", DEFAULT_DUST_OVERRIDES, "List of materials that should be dusts."));

		mainConfig.setComment("tags", "Configurations related to tags.");

		mainConfig.setComment("recipes", "Configurations related to recipes.");

		mainConfig.setComment("advancements", "Configurations related to advancements.");

		mainConfig.setComment("colors", "Configurations related to color generation.");
		gammaValue = mainConfig.getDefinedDouble("colors.gammaValue", gammaValue, "Gamma value used to blend colors.");
	}

	public static void setupMaterialConfigs() {
		materialConfigDir = new File(configDir, "materials");
		if(!materialConfigDir.exists() || !materialConfigDir.isDirectory()) {
			try {
				if(materialConfigDir.exists() && !materialConfigDir.isDirectory()) {
					LOGGER.warn("Config directory {} is a file, deleting", materialConfigDir);
					materialConfigDir.delete();
				}
				if(!materialConfigDir.mkdir()) {
					throw new Error("Could not create config directory "+materialConfigDir);
				}
			}
			catch(SecurityException e) {
				throw new Error("Could not create config directory "+materialConfigDir, e);
			}
		}
		MATERIAL_CONFIGS.clear();
		for(Material material : MaterialHandler.getMaterials()) {
			if(material.getType().isNone()) {
				continue;
			}
			IDynamicSpecConfig config = new DynamicSpecConfig(CommentedFileConfig.builder(new File(materialConfigDir, material.getName()+".toml")).sync().autosave().build());
			MATERIAL_CONFIGS.put(material, config);
			material.setConfig(config);
		}
	}

	public static void setupModuleConfigsPre() {
		moduleConfigDir = new File(configDir, "modules");
		if(!moduleConfigDir.exists() || !moduleConfigDir.isDirectory()) {
			try {
				if(moduleConfigDir.exists() && !moduleConfigDir.isDirectory()) {
					LOGGER.warn("Config directory {} is a file, deleting", moduleConfigDir);
					moduleConfigDir.delete();
				}
				if(!moduleConfigDir.mkdir()) {
					throw new Error("Could not create config directory "+moduleConfigDir);
				}
			}
			catch(SecurityException e) {
				throw new Error("Could not create config directory "+moduleConfigDir, e);
			}
		}
		for(IModule module : ModuleHandler.getModules()) {
			IDynamicSpecConfig config = new DynamicSpecConfig(CommentedFileConfig.builder(new File(moduleConfigDir, module.getName()+".toml")).sync().autosave().build());
			MODULE_CONFIGS.put(module, config);
			ModuleData data = ModuleHandler.getModuleData(module);
			data.setConfig(config);
			module.defineModuleConfigPre(data, config);
			module.defineMaterialConfigPre(data, Collections.unmodifiableNavigableMap(MATERIAL_CONFIGS));
		}
	}

	public static void setupModuleConfigs() {
		for(IModule module : ModuleHandler.getModules()) {
			IDynamicSpecConfig config = MODULE_CONFIGS.get(module);
			ModuleData data = ModuleHandler.getModuleData(module);
			module.defineModuleConfig(data, config);
			module.defineMaterialConfig(data, Collections.unmodifiableNavigableMap(Maps.filterKeys(MATERIAL_CONFIGS, data.getMaterials()::contains)));
		}
	}
}
