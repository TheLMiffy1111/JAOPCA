package thelm.jaopca.api.materials;

import java.util.Locale;

public enum EnumMaterialType {

	INGOT("ingots"), GEM("gems"), CRYSTAL("crystals"), DUST("dusts"),
	INGOT_PLAIN("ingots"), GEM_PLAIN("gems"), CRYSTAL_PLAIN("crystals"), DUST_PLAIN("dusts"),
	NONE("");

	public static final EnumMaterialType[] INGOTS = {INGOT, INGOT_PLAIN};
	public static final EnumMaterialType[] GEMS = {GEM, GEM_PLAIN};
	public static final EnumMaterialType[] CRYSTALS = {CRYSTAL, CRYSTAL_PLAIN};
	public static final EnumMaterialType[] DUSTS = {DUST, DUST_PLAIN};
	public static final EnumMaterialType[] NON_DUSTS = {INGOT, GEM, CRYSTAL, INGOT_PLAIN, GEM_PLAIN, CRYSTAL_PLAIN};
	public static final EnumMaterialType[] ORE = {INGOT, GEM, CRYSTAL, DUST};

	private final String formName;

	EnumMaterialType(String formName) {
		this.formName = formName;
	}

	public String getName() {
		return name().toLowerCase(Locale.US);
	}

	public String getFormName() {
		return formName;
	}

	public boolean isNone() {
		return this == NONE;
	}

	public static EnumMaterialType fromName(String name) {
		for(EnumMaterialType type : values()) {
			if(type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
