package thelm.jaopca.api.materials;

import java.util.Arrays;
import java.util.Locale;

public enum MaterialType {

	INGOT("ingots"),
	INGOT_LEGACY("ingots"),
	GEM("gems"),
	CRYSTAL("crystals"),
	DUST("dusts"),
	INGOT_PLAIN("ingots"),
	GEM_PLAIN("gems"),
	CRYSTAL_PLAIN("crystals"),
	DUST_PLAIN("dusts");

	public static final MaterialType[] INGOTS = {INGOT, INGOT_LEGACY, INGOT_PLAIN};
	public static final MaterialType[] GEMS = {GEM, GEM_PLAIN};
	public static final MaterialType[] CRYSTALS = {CRYSTAL, CRYSTAL_PLAIN};
	public static final MaterialType[] DUSTS = {DUST, DUST_PLAIN};
	public static final MaterialType[] NON_DUSTS = {INGOT, INGOT_LEGACY, GEM, CRYSTAL, INGOT_PLAIN, GEM_PLAIN, CRYSTAL_PLAIN};
	public static final MaterialType[] ORE = {INGOT, INGOT_LEGACY, GEM, CRYSTAL, DUST};

	private final String formName;

	MaterialType(String formName) {
		this.formName = formName;
	}

	public String getName() {
		return name().toLowerCase(Locale.US);
	}

	public String getFormName() {
		return formName;
	}

	public static MaterialType fromName(String name) {
		return Arrays.stream(values()).filter(t->t.getName().equalsIgnoreCase(name)).findAny().orElse(null);
	}
}
