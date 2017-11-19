package thelm.jaopca.crafttweaker;

import java.util.List;
import java.util.stream.Collectors;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.JAOPCAApi;

@ZenClass("mods.jaopca.JAOPCA")
@ZenRegister
public class JAOPCAAccess {

	@ZenMethod
	public static boolean containsEntry(String entryName) {
		return JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.containsKey(entryName);
	}

	@ZenMethod
	public static OreEntryAccess getOre(String oreName) {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().filter(entry->entry.getOreName().equals(oreName)).map(OreEntryAccess::new).findAny().orElse(null);
	}

	@ZenMethod
	public static List<OreEntryAccess> getOresForEntry(String entryName) {
		return JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entryName).stream().map(OreEntryAccess::new).collect(Collectors.toList());
	}

	@ZenMethod
	public static List<OreEntryAccess> getOresForType(String oreType) {
		return JAOPCAApi.ORE_TYPE_TO_ORES_MAP.get(EnumOreType.fromName(oreType)).stream().map(OreEntryAccess::new).collect(Collectors.toList());
	}

	@ZenMethod
	public static List<OreEntryAccess> getAllOres() {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().map(OreEntryAccess::new).collect(Collectors.toList());
	}
}
