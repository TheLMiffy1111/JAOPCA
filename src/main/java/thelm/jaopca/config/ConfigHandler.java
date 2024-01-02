package thelm.jaopca.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.data.DataCollector;
import thelm.jaopca.materials.Material;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleData;
import thelm.jaopca.modules.ModuleHandler;

public class ConfigHandler {

	private ConfigHandler() {}

	private static final Logger LOGGER = LogManager.getLogger();
	private static Path configDir;
	private static Path customFormConfigFile;
	private static Path materialConfigDir;
	private static Path moduleConfigDir;
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

	public static final List<String> DEFAULT_GEM_OVERRIDES = Lists.newArrayList("diamond", "emerald", "lapis", "prismarine", "quartz");
	public static final List<String> DEFAULT_CRYSTAL_OVERRIDES = Lists.newArrayList();
	public static final List<String> DEFAULT_DUST_OVERRIDES = Lists.newArrayList("glowstone", "redstone");
	public static final Set<String> GEM_OVERRIDES = new TreeSet<>();
	public static final Set<String> CRYSTAL_OVERRIDES = new TreeSet<>();
	public static final Set<String> DUST_OVERRIDES = new TreeSet<>();

	private static final List<String> DEFAULT_PREFERRED_MODS = Lists.newArrayList("minecraft");
	public static final List<String> PREFERRED_MODS = new ArrayList<>();

	public static final Set<ResourceLocation> BLOCK_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> ITEM_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> FLUID_TAG_BLACKLIST = new TreeSet<>();
	public static final Set<ResourceLocation> ENTITY_TYPE_TAG_BLACKLIST = new TreeSet<>();

	public static final Set<ResourceLocation> RECIPE_BLACKLIST = new TreeSet<>();
	public static final List<Pattern> RECIPE_REGEX_BLACKLIST = new ArrayList<>();

	public static final Set<ResourceLocation> LOOT_TABLE_BLACKLIST = new TreeSet<>();

	public static final Set<ResourceLocation> ADVANCEMENT_BLACKLIST = new TreeSet<>();

	public static final Set<String> DATA_MODULE_BLACKLIST = new TreeSet<>();

	public static double gammaValue = 2;
	public static boolean resetColors = false;

	public static boolean checkL10nUpdates = true;
	public static double updateInterval = 3;

	public static void setupMainConfig() {
		configDir = FMLPaths.CONFIGDIR.get().resolve("jaopca");
		if(!Files.exists(configDir) || !Files.isDirectory(configDir)) {
			try {
				if(Files.exists(configDir) && !Files.isDirectory(configDir)) {
					LOGGER.warn("Config directory {} is a file, deleting", configDir);
					Files.delete(configDir);
				}
				Files.createDirectory(configDir);
			}
			catch(Exception e) {
				throw new RuntimeException("Could not create config directory "+configDir, e);
			}
		}

		mainConfig = new DynamicSpecConfig(CommentedFileConfig.builder(configDir.resolve("main.toml")).sync().backingMapCreator(LinkedHashMap::new).autosave().build());

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

		mainConfig.setComment("itemSelection", "Configurations related to item selection.");
		PREFERRED_MODS.addAll(mainConfig.getDefinedStringList("itemSelection.preferredMods", DEFAULT_PREFERRED_MODS, "List of mods that are preferred when selecting items in recipes."));

		mainConfig.setComment("blockTags", "Configurations related to block tags.");
		BLOCK_TAG_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("blockTags.blacklist", new ArrayList<>(),
				"List of block tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags("blocks").addAll(Lists.transform(mainConfig.getDefinedStringList("blockTags.customDefined", new ArrayList<>(),
				"List of block tags that should be considered as defined."), ResourceLocation::new));

		mainConfig.setComment("itemTags", "Configurations related to item tags.");
		ITEM_TAG_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("itemTags.blacklist", new ArrayList<>(),
				"List of item tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags("items").addAll(Lists.transform(mainConfig.getDefinedStringList("itemTags.customDefined", new ArrayList<>(),
				"List of item tags that should be considered as defined."), ResourceLocation::new));

		mainConfig.setComment("fluidTags", "Configurations related to fluid tags.");
		FLUID_TAG_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("fluidTags.blacklist", new ArrayList<>(),
				"List of fluid tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags("fluids").addAll(Lists.transform(mainConfig.getDefinedStringList("fluidTags.customDefined", new ArrayList<>(),
				"List of fluid tags that should be considered as defined."), ResourceLocation::new));

		mainConfig.setComment("entityTypeTags", "Configurations related to entity type tags.");
		ENTITY_TYPE_TAG_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("entityTypeTags.blacklist", new ArrayList<>(),
				"List of entity type tags that should not be added."), ResourceLocation::new));
		DataCollector.getDefinedTags("entity_types").addAll(Lists.transform(mainConfig.getDefinedStringList("entityTypeTags.customDefined", new ArrayList<>(),
				"List of entity type tags that should be considered as defined."), ResourceLocation::new));

		mainConfig.setComment("recipes", "Configurations related to recipes.");
		RECIPE_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("recipes.blacklist", new ArrayList<>(),
				"List of recipes that should not be added."), ResourceLocation::new));
		RECIPE_REGEX_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("recipes.regexBlacklist", new ArrayList<>(),
				"List of recipes by regex that should not be added."), Pattern::compile));

		mainConfig.setComment("lootTables", "Configurations related to loot tables.");
		LOOT_TABLE_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("lootTables.blacklist", new ArrayList<>(),
				"List of loot tables that should not be added."), ResourceLocation::new));

		mainConfig.setComment("advancements", "Configurations related to advancements.");
		ADVANCEMENT_BLACKLIST.addAll(Lists.transform(mainConfig.getDefinedStringList("advancements.blacklist", new ArrayList<>(),
				"List of advancements that should not be added."), ResourceLocation::new));

		mainConfig.setComment("data", "Configurations related to data modules.");
		DATA_MODULE_BLACKLIST.addAll(mainConfig.getDefinedStringList("data.moduleBlacklist", new ArrayList<>(), "List of data modules that should not be registered."));

		mainConfig.setComment("colors", "Configurations related to color generation.");
		gammaValue = mainConfig.getDefinedDouble("colors.gammaValue", gammaValue, "The gamma value used to blend colors.");
		resetColors = mainConfig.getDefinedBoolean("colors.resetColors", false, "Should colors of all materials be reset on next startup.");
		mainConfig.set("colors.resetColors", false);

		mainConfig.setComment("materialLocalization", "Configurations related to material localization.");
		checkL10nUpdates = mainConfig.getDefinedBoolean("materialLocalization.checkL10nUpdates", checkL10nUpdates, "Should the mod check for material localization updates.");
		updateInterval = mainConfig.getDefinedDouble("materialLocalization.updateInterval", updateInterval, "The update interval of localization files in days.");
	}

	public static void setupCustomFormConfig() {
		customFormConfigFile = configDir.resolve("custom_forms.json");
		try {
			if(!Files.exists(customFormConfigFile)) {
				Files.createFile(customFormConfigFile);
			}
		}
		catch(Exception e) {
			throw new RuntimeException("Could not create config file "+customFormConfigFile, e);
		}
		CustomModule.instance.setCustomFormConfigFile(customFormConfigFile);
	}

	public static void setupMaterialConfigs() {
		materialConfigDir = configDir.resolve("materials");
		if(!Files.exists(materialConfigDir) || !Files.isDirectory(materialConfigDir)) {
			try {
				if(Files.exists(materialConfigDir) && !Files.isDirectory(materialConfigDir)) {
					LOGGER.warn("Config directory {} is a file, deleting", materialConfigDir);
					Files.delete(materialConfigDir);
				}
				Files.createDirectory(materialConfigDir);
			}
			catch(Exception e) {
				throw new RuntimeException("Could not create config directory "+materialConfigDir, e);
			}
		}
		MATERIAL_CONFIGS.clear();
		for(Material material : MaterialHandler.getMaterials()) {
			IDynamicSpecConfig config = new DynamicSpecConfig(CommentedFileConfig.builder(materialConfigDir.resolve(material.getName()+".toml")).sync().backingMapCreator(LinkedHashMap::new).autosave().build());
			MATERIAL_CONFIGS.put(material, config);
			material.setConfig(config);
		}
	}

	public static void setupModuleConfigsPre() {
		moduleConfigDir = configDir.resolve("modules");
		if(!Files.exists(moduleConfigDir) || !Files.isDirectory(moduleConfigDir)) {
			try {
				if(Files.exists(moduleConfigDir) && !Files.isDirectory(moduleConfigDir)) {
					LOGGER.warn("Config directory {} is a file, deleting", moduleConfigDir);
					Files.delete(moduleConfigDir);
				}
				Files.createDirectory(moduleConfigDir);
			}
			catch(Exception e) {
				throw new RuntimeException("Could not create config directory "+moduleConfigDir, e);
			}
		}
		for(IModule module : ModuleHandler.getModules()) {
			IDynamicSpecConfig config = new DynamicSpecConfig(CommentedFileConfig.builder(moduleConfigDir.resolve(module.getName()+".toml")).sync().backingMapCreator(LinkedHashMap::new).autosave().build());
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
			module.defineMaterialConfig(data, Collections.unmodifiableNavigableMap(MATERIAL_CONFIGS));
		}
	}
}
