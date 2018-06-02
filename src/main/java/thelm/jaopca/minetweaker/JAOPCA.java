package thelm.jaopca.minetweaker;

import java.util.List;
import java.util.stream.Collectors;

import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.JAOPCAApi;

@ZenClass("mods.jaopca.JAOPCA")
public class JAOPCA {

	@ZenMethod
	public static boolean containsEntry(String entryName) {
		return JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.containsKey(entryName);
	}

	@ZenMethod
	public static OreEntry getOre(String oreName) {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().filter(entry->entry.getOreName().equals(oreName)).map(OreEntry::new).findAny().orElse(null);
	}

	@ZenMethod
	public static List<OreEntry> getOresForEntry(String entryName) {
		return JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entryName).stream().map(OreEntry::new).collect(Collectors.toList());
	}

	@ZenMethod
	public static List<OreEntry> getOresForType(String oreType) {
		return JAOPCAApi.ORE_TYPE_TO_ORES_MAP.get(EnumOreType.fromName(oreType)).stream().map(OreEntry::new).collect(Collectors.toList());
	}

	@ZenMethod
	public static List<OreEntry> getAllOres() {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().map(OreEntry::new).collect(Collectors.toList());
	}
}
