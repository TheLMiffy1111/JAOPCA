package thelm.oredictinit.api;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.google.common.collect.Lists;

public class OreDictInitApi {

	/**
	 * List of ore dict entry compats
	 */
	public static final ArrayList<ICompat> ORE_DICT_COMPAT_LIST = Lists.<ICompat>newArrayList();

	public static void addToJAOPCABlacklist(String name) {
		try {
			Class<?> oreFinderClass = Class.forName("thelm.jaopca.ore.OreFinder");
			Field blacklistField = oreFinderClass.getField("CONTAINING_BLACKLIST");
			ArrayList<String> blacklist = (ArrayList<String>)blacklistField.get(null);
			blacklist.add(name);
		}
		catch(ClassNotFoundException e) {}
		catch(NoSuchFieldException e) {
			e.printStackTrace();
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
