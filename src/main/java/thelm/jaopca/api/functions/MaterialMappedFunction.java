package thelm.jaopca.api.functions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;

public final class MaterialMappedFunction<T> implements Function<IMaterial, T> {

	public final T defaultValue;
	public final Map<MaterialType, T> materialTypes;
	public final Map<IMaterial, T> materials;
	public final String path;
	public final String comment;
	public final Map<IMaterial, T> configMaterials;
	public final Function<String, T> nameToValue;
	public final Function<T, String> valueToName;

	private MaterialMappedFunction(T defaultValue, Map<MaterialType, T> materialTypes, Map<IMaterial, T> materials, String path, String comment, Function<String, T> nameToValue, Function<T, String> valueToName) {
		this.defaultValue = defaultValue;
		this.materialTypes = Objects.requireNonNull(materialTypes);
		this.materials = Objects.requireNonNull(materials);
		this.path = Strings.nullToEmpty(path);
		this.comment = Strings.nullToEmpty(comment);
		this.nameToValue = Objects.requireNonNull(nameToValue);
		this.valueToName = Objects.requireNonNull(valueToName);
		configMaterials = !path.isEmpty() ? new HashMap<>() : Map.of();
	}

	public static <T> MaterialMappedFunction<T> of(T defaultValue, Map<MaterialType, T> materialTypes, Map<IMaterial, T> materials, String path, String comment, Function<String, T> nameToValue, Function<T, String> valueToName) {
		return new MaterialMappedFunction<>(defaultValue, materialTypes, materials, path, comment, nameToValue, valueToName);
	}

	public static <T> MaterialMappedFunction<T> of(T defaultValue, Map<MaterialType, T> materialTypes, Map<IMaterial, T> materials, Function<String, T> nameToValue, Function<T, String> valueToName) {
		return of(defaultValue, materialTypes, materials, "", "", nameToValue, valueToName);
	}

	public static <T> MaterialMappedFunction<T> of(T defaultValue, String path, String comment, Function<String, T> nameToValue, Function<T, String> valueToName) {
		return of(defaultValue, Map.of(), Map.of(), path, comment, nameToValue, valueToName);
	}

	public static <T> MaterialMappedFunction<T> of(T defaultValue, Function<String, T> nameToValue, Function<T, String> valueToName) {
		return of(defaultValue, Map.of(), Map.of(), "", "", nameToValue, valueToName);
	}

	public static <T> MaterialMappedFunction<T> of(T defaultValue, Function<IMaterial, T> function, Function<String, T> nameToValue, Function<T, String> valueToName) {
		if(function instanceof MaterialMappedFunction<T> mf) {
			return of(defaultValue, mf.materialTypes, mf.materials, mf.path, mf.comment, nameToValue, valueToName);
		}
		if(function instanceof MaterialFunction<T> mf) {
			return of(defaultValue, mf.materialTypes, mf.materials, nameToValue, valueToName);
		}
		Map<IMaterial, T> materialMap = JAOPCAApi.instance().getMaterials().stream().collect(Collectors.toMap(Function.identity(), function::apply));
		return of(defaultValue, Map.of(), materialMap, nameToValue, valueToName);
	}

	public static MaterialMappedFunction<String> of(String defaultValue, Map<MaterialType, String> materialTypes, Map<IMaterial, String> materials, String path, String comment) {
		return of(defaultValue, materialTypes, materials, path, comment, Function.identity(), Function.identity());
	}

	public static MaterialMappedFunction<String> of(String defaultValue, Map<MaterialType, String> materialTypes, Map<IMaterial, String> materials) {
		return of(defaultValue, materialTypes, materials, "", "");
	}

	public static MaterialMappedFunction<String> of(String defaultValue, String path, String comment) {
		return of(defaultValue, Map.of(), Map.of(), path, comment);
	}

	public static MaterialMappedFunction<String> of(String defaultValue) {
		return of(defaultValue, Map.of(), Map.of(), "", "");
	}

	public static MaterialMappedFunction<String> of(String defaultValue, Function<IMaterial, String> function) {
		if(function instanceof MaterialMappedFunction<String> mf) {
			return of(defaultValue, mf.materialTypes, mf.materials, mf.path, mf.comment);
		}
		if(function instanceof MaterialFunction<String> mf) {
			return of(defaultValue, mf.materialTypes, mf.materials);
		}
		Map<IMaterial, String> materialMap = JAOPCAApi.instance().getMaterials().stream().collect(Collectors.toMap(Function.identity(), function::apply));
		return of(defaultValue, Map.of(), materialMap);
	}

	public static <T extends Enum<T>> MaterialMappedFunction<T> of(Class<T> enumClass, T defaultValue, Map<MaterialType, T> materialTypes, Map<IMaterial, T> materials, String path, String comment) {
		return of(defaultValue, materialTypes, materials, path, comment,
				name->Arrays.stream(enumClass.getEnumConstants()).filter(value->value.name().equalsIgnoreCase(name)).findAny().orElse(null),
				value->value == null ? "" : value.name().toLowerCase(Locale.US));
	}

	public static <T extends Enum<T>> MaterialMappedFunction<T> of(Class<T> enumClass, T defaultValue, Map<MaterialType, T> materialTypes, Map<IMaterial, T> materials) {
		return of(enumClass, defaultValue, materialTypes, materials, "", "");
	}

	public static <T extends Enum<T>> MaterialMappedFunction<T> of(Class<T> enumClass, T defaultValue, String path, String comment) {
		return of(enumClass, defaultValue, Map.of(), Map.of(), path, comment);
	}

	public static <T extends Enum<T>> MaterialMappedFunction<T> of(Class<T> enumClass, T defaultValue) {
		return of(enumClass, defaultValue, Map.of(), Map.of(), "", "");
	}

	public static <T extends Enum<T>> MaterialMappedFunction<T> of(Class<T> enumClass, T defaultValue, Function<IMaterial, T> function) {
		if(function instanceof MaterialMappedFunction<T> mf) {
			return of(enumClass, defaultValue, mf.materialTypes, mf.materials, mf.path, mf.comment);
		}
		if(function instanceof MaterialFunction<T> mf) {
			return of(enumClass, defaultValue, mf.materialTypes, mf.materials);
		}
		Map<IMaterial, T> materialMap = JAOPCAApi.instance().getMaterials().stream().collect(Collectors.toMap(Function.identity(), function::apply));
		return of(enumClass, defaultValue, Map.of(), materialMap);
	}

	@Override
	public T apply(IMaterial material) {
		if(!path.isEmpty()) {
			return configMaterials.computeIfAbsent(material,
					m->nameToValue.apply(JAOPCAApi.instance().getMaterialConfig(material).getDefinedString(path, valueToName.apply(applyUnconfigured(material)), comment)));
		}
		return applyUnconfigured(material);
	}

	public T applyUnconfigured(IMaterial material) {
		if(materials.containsKey(material)) {
			return materials.get(material);
		}
		if(materialTypes.containsKey(material.getType())) {
			return materialTypes.get(material.getType());
		}
		return defaultValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultValue, materialTypes, materials, path, nameToValue, valueToName);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MaterialMappedFunction other) {
			return Objects.equals(defaultValue, other.defaultValue) &&
					materialTypes.equals(other.materialTypes) &&
					materials.equals(other.materials) &&
					path.equals(other.path) &&
					nameToValue.equals(other.nameToValue) &&
					valueToName.equals(other.valueToName);
		}
		return false;
	}
}
