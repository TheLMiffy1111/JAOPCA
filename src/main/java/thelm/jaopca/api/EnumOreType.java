package thelm.jaopca.api;

public enum EnumOreType {

	INGOT,
	GEM,
	DUST,
	INGOT_ORELESS,
	GEM_ORELESS;

	public static final EnumOreType[] DUSTLESS = new EnumOreType[] {INGOT, GEM, INGOT_ORELESS, GEM_ORELESS};
	public static final EnumOreType[] INGOTS = new EnumOreType[] {INGOT, INGOT_ORELESS};
	public static final EnumOreType[] GEMS = new EnumOreType[] {GEM, GEM_ORELESS};
	public static final EnumOreType[] ORE = new EnumOreType[] {INGOT, GEM, DUST};

	public static EnumOreType fromName(String name) {
		for(EnumOreType type : values()) {
			if(type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
