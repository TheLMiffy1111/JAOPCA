package thelm.jaopca.api.materials;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TextureType {

	METALLIC;

	private static final Map<String, TextureType> NAME_LOOKUP;

	public String getName() {
		return name().toLowerCase(Locale.US);
	}

	public String getRegistryName() {
		return name().toLowerCase(Locale.US)+'/';
	}

	public static boolean containsName(String name) {
		return NAME_LOOKUP.containsKey(name);
	}

	public static TextureType byName(String name) {
		return NAME_LOOKUP.getOrDefault(name.toLowerCase(Locale.US), METALLIC);
	}

	static {
		NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(TextureType::getName, Function.identity()));
	}
}
