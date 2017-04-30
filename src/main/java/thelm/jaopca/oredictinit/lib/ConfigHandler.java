package thelm.jaopca.oredictinit.lib;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	
	public static Configuration configFile;
	public static Set<String> usedCategories = new HashSet<String>();
	public static int lines;
	
	public static void preInit(File file) {
		configFile = new Configuration(file, true);
		
		initLines();
		initThing();
		initCompat();
		usedCategories.add("Custom");
		usedCategories.add("Compat");
	}	
	
	private static void initLines() {
		
		lines = getIntegerWithComment("Custom", "amountlines", 1, "Lines of block/item entries");
		
		if (configFile.hasChanged())
			configFile.save();
	}
	
	private static void initThing() {
		
		Data.definedThingyBlocks.add(0, getStringWithComment("Custom", "blocks0", "", "Format: oreDictEntry,modID,Block,damageValue;oreDictEntry,modID,Block,damageValue;etc."));
		Data.definedThingyItems.add(0, getStringWithComment("Custom", "items0", "", "Format: oreDictEntry,modID,Item,damageValue;oreDictEntry,modID,Item,damageValue;etc."));
		
		for(int i = 1; i < lines; i++){
			Data.definedThingyBlocks.add(i, getString("Custom", "blocks" + i, ""));
			Data.definedThingyItems.add(i, getString("Custom", "items" + i, ""));
		}
		
		if(configFile.hasChanged())
			configFile.save();
	}
	
	private static void initCompat() {
		
		//Nothing to see here yet :D
		
		if (configFile.hasChanged())
			configFile.save();
	}
	
	private static String getString(String category, String name, String def) {
		return configFile.get(category, name, def).setRequiresMcRestart(true).getString();
	}
	
	private static String getStringWithComment(String category, String name, String def, String comment) {
		return configFile.get(category, name, def, comment).setRequiresMcRestart(true).getString();
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
