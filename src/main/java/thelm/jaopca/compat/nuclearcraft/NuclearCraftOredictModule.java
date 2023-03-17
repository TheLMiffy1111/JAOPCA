package thelm.jaopca.compat.nuclearcraft;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import thelm.jaopca.api.oredict.IOredictModule;
import thelm.jaopca.api.oredict.JAOPCAOredictModule;
import thelm.jaopca.utils.ApiImpl;

@JAOPCAOredictModule(modDependencies = "nuclearcraft")
public class NuclearCraftOredictModule implements IOredictModule {

	public NuclearCraftOredictModule() {
		String[] names = Stream.of(
				"Americium241", "Americium242", "Americium243",
				"Berkelium247", "Berkelium248",
				"Californium249", "Californium250", "Californium251", "Californium252",
				"Copernicium291",
				"Curium243", "Curium245", "Curium246", "Curium247",
				"Neptunium236", "Neptunium237",
				"Plutonium238", "Plutonium239", "Plutonium241", "Plutonium242",
				"Thorium230", "Thorium232",
				"Uranium233", "Uranium235", "Uranium238").
				flatMap(name->Stream.of(name+"All", name+"Base")).
				toArray(String[]::new);
		ApiImpl.INSTANCE.registerBlacklistedMaterialNames(names);
	}

	@Override
	public String getName() {
		return "nuclearcraft";
	}

	@Override
	public void register() {
		List<String> formats = Arrays.asList(
				"%s", "%sCarbide", "%sNitride", "%sOxide", "%sTRISO", "%sZA",
				"Depleted%sNitride", "Depleted%sOxide", "Depleted%sTRISO", "Depleted%sZA");
		String[] names = Stream.of(
				"HEA242", "HEB248", "HECf249", "HECf251", "HECm243", "HECm245", "HECm247", "HEN236",
				"HEP239", "HEP241", "HEU233", "HEU235", "LEA242", "LEB248", "LECf249", "LECf251",
				"LECm243", "LECm245", "LECm247", "LEN236", "LEP239", "LEP241", "LEU233", "LEU235",
				"MIX239", "MIX241", "MIX291", "TBU").
				flatMap(name->formats.stream().map(format->String.format(format, name))).
				toArray(String[]::new);
		ApiImpl.INSTANCE.registerBlacklistedMaterialNames(names);
		ApiImpl.INSTANCE.registerBlacklistedMaterialNames("TBP");
	}
}
