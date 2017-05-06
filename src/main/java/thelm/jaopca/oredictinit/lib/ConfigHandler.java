package thelm.jaopca.oredictinit.lib;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration configFile;
	public static Set<String> usedCategories = new HashSet<String>();

	public static void preInit(File file) {
		configFile = new Configuration(file, true);

		initThing();
		initCompat();
		usedCategories.add("Custom");
		usedCategories.add("Compat");
	}

	private static void initThing() {
		Data.definedThingyBlocks.addAll(Arrays.asList(getStringArrayWithComment("Custom", "blocks", new String[0], "Format: oreDictEntry,modID,Block,damageValue;oreDictEntry,modID,Block,damageValue;etc.")));
		Data.definedThingyItems.addAll(Arrays.asList(getStringArrayWithComment("Custom", "items", new String[0], "Format: oreDictEntry,modID,Item,damageValue;oreDictEntry,modID,Item,damageValue;etc.")));

		if(configFile.hasChanged())
			configFile.save();
	}

	private static void initCompat() {
		//Nothing to see here yet :D

		if(configFile.hasChanged())
			configFile.save();
	}

	private static String[] getStringArrayWithComment(String category, String name, String[] def, String comment) {
		return configFile.get(category, name, def, comment).setRequiresMcRestart(true).getStringList();
	}

	private static int getIntegerWithComment(String category, String name, int def, String comment) {
		return configFile.get(category, name, def, comment).setRequiresMcRestart(true).getInt(def);
	}

	private boolean getBoolean(String category, String name, boolean def) {
		return configFile.get(category, name, def).setRequiresMcRestart(true).getBoolean(def);
	}

	private boolean getBooleanWithComment(String category, String name, boolean def, String comment) {
		return configFile.get(category, name, def, comment).setRequiresMcRestart(true).getBoolean(def);
	}
}
