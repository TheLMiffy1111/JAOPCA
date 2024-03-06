package thelm.jaopca.api.functions;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;

public final class MaterialFunction<T> implements Function<IMaterial, T> {

	public final T defaultValue;
	public final Map<MaterialType, T> materialTypes;
	public final Map<IMaterial, T> materials;

	private MaterialFunction(T defaultValue, Map<MaterialType, T> materialTypes, Map<IMaterial, T> materials) {
		this.defaultValue = defaultValue;
		this.materialTypes = Objects.requireNonNull(materialTypes);
		this.materials = Objects.requireNonNull(materials);
	}

	public static <T> MaterialFunction<T> of(T defaultValue, Map<MaterialType, T> materialTypes, Map<IMaterial, T> materials) {
		return new MaterialFunction<>(defaultValue, materialTypes, materials);
	}

	public static <T> MaterialFunction<T> of(T defaultValue) {
		return of(defaultValue, Map.of(), Map.of());
	}

	public static <T> MaterialFunction<T> of(T defaultValue, Function<IMaterial, T> function) {
		if(function instanceof MaterialFunction<T> mf) {
			return of(defaultValue, mf.materialTypes, mf.materials);
		}
		if(function instanceof MaterialMappedFunction<T> mf) {
			return of(defaultValue, mf.materialTypes, mf.materials);
		}
		Map<IMaterial, T> materialMap = JAOPCAApi.instance().getMaterials().stream().collect(Collectors.toMap(Function.identity(), function::apply));
		return of(defaultValue, Map.of(), materialMap);
	}

	@Override
	public T apply(IMaterial material) {
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
		return Objects.hash(defaultValue, materialTypes, materials);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MaterialFunction other) {
			return Objects.equals(defaultValue, other.defaultValue) &&
					materialTypes.equals(other.materialTypes) &&
					materials.equals(other.materials);
		}
		return false;
	}
}
