package thelm.oredictinit.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import thelm.oredictinit.api.ICompat;
import thelm.oredictinit.api.OreDictInitApi;

public class ConfigHandler {

	public static Configuration configFile;
	public static Set<String> usedCategories = new HashSet<String>();

	public static void preInit(File file) {
		configFile = new Configuration(file, true);

		initThing();
		initCompat();
		usedCategories.add("custom");
		usedCategories.add("compat");
	}

	private static void initThing() {
		Data.definedThingyBlocks.addAll(Arrays.asList(getStringArrayWithComment("custom", "blocks", new String[0], "Format: oreDictEntry,modID,Block,damageValue;oreDictEntry,modID,Block,damageValue;etc.")));
		Data.definedThingyItems.addAll(Arrays.asList(getStringArrayWithComment("custom", "items", new String[0], "Format: oreDictEntry,modID,Item,damageValue;oreDictEntry,modID,Item,damageValue;etc.")));

		if(configFile.hasChanged())
			configFile.save();
	}

	private static void initCompat() {
		List<String> blacklist = Arrays.<String>asList(getStringArray("compat", "blacklist", new String[0]));
		ArrayList<ICompat> toRemove = Lists.<ICompat>newArrayList();

		for(ICompat compat : OreDictInitApi.ORE_DICT_COMPAT_LIST) {
			if(blacklist.contains(compat.getName())) {
				toRemove.add(compat);
			}
		}

		OreDictInitApi.ORE_DICT_COMPAT_LIST.removeAll(toRemove);

		if(configFile.hasChanged())
			configFile.save();
	}

	private static String[] getStringArray(String category, String name, String[] def) {
		return configFile.get(category, name, def).setRequiresMcRestart(true).getStringList();
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
