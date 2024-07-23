package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;

public final class MaterialPredicate implements Predicate<IMaterial> {

	public final boolean defaultValue;
	public final Object2BooleanMap<MaterialType> materialTypes;
	public final Object2BooleanMap<IMaterial> materials;
	public final String path;
	public final String comment;
	public final Object2BooleanMap<IMaterial> configMaterials;

	private MaterialPredicate(boolean defaultValue, Object2BooleanMap<MaterialType> materialTypes, Object2BooleanMap<IMaterial> materials, String path, String comment) {
		this.defaultValue = defaultValue;
		this.materialTypes = Objects.requireNonNull(materialTypes);
		this.materials = Objects.requireNonNull(materials);
		this.path = Strings.nullToEmpty(path);
		this.comment = Strings.nullToEmpty(comment);
		configMaterials = !path.isEmpty() ? new Object2BooleanOpenHashMap<>() : Object2BooleanMaps.emptyMap();
	}

	public static MaterialPredicate of(boolean defaultValue, Object2BooleanMap<MaterialType> materialTypes, Object2BooleanMap<IMaterial> materials, String path, String comment) {
		return new MaterialPredicate(defaultValue, materialTypes, materials, path, comment);
	}

	public static MaterialPredicate of(boolean defaultValue, Object2BooleanMap<MaterialType> materialTypes, Object2BooleanMap<IMaterial> materials) {
		return of(defaultValue, materialTypes, materials, "", "");
	}

	public static MaterialPredicate of(boolean defaultValue, String path, String comment) {
		return of(defaultValue, Object2BooleanMaps.emptyMap(), Object2BooleanMaps.emptyMap(), path, comment);
	}

	public static MaterialPredicate of(boolean defaultValue) {
		return of(defaultValue, Object2BooleanMaps.emptyMap(), Object2BooleanMaps.emptyMap(), "", "");
	}

	public static MaterialPredicate of(boolean defaultValue, Predicate<IMaterial> function) {
		if(function instanceof MaterialPredicate mf) {
			return of(defaultValue, mf.materialTypes, mf.materials, mf.path, mf.comment);
		}
		Object2BooleanMap<IMaterial> materialMap = JAOPCAApi.instance().getMaterials().stream().collect(
				Collectors.toMap(Function.identity(), function::test, (a, b)->a, Object2BooleanOpenHashMap::new));
		return of(defaultValue, Object2BooleanMaps.emptyMap(), materialMap, "", "");
	}

	@Override
	public boolean test(IMaterial material) {
		if(!path.isEmpty()) {
			return configMaterials.computeIfAbsent(material,
					m->JAOPCAApi.instance().getMaterialConfig(material).getDefinedBoolean(path, testUnconfigured(material), comment));
		}
		return testUnconfigured(material);
	}

	public boolean testUnconfigured(IMaterial material) {
		if(materials.containsKey(material)) {
			return materials.getBoolean(material);
		}
		if(materialTypes.containsKey(material.getType())) {
			return materialTypes.getBoolean(material.getType());
		}
		return defaultValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultValue, materialTypes, materials, path);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MaterialPredicate other) {
			return defaultValue == other.defaultValue &&
					materialTypes.equals(other.materialTypes) &&
					materials.equals(other.materials) &&
					path.equals(other.path);
		}
		return false;
	}
}
