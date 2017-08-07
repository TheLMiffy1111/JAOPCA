package thelm.jaopca.api;

public enum EnumEntryType {

	ITEM("item"),
	BLOCK("block"),
	FLUID("fluid"),
	CUSTOM("custom");
	
	String name;
	
	EnumEntryType(String name) {
		this.name = name;
	}
	
	public static EnumEntryType fromName(String name) {
		for(EnumEntryType type : values()) {
			
		}
		return null;
	}
}
