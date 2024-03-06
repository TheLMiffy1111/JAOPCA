package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;

public final class MaterialDoubleFunction implements ToDoubleFunction<IMaterial> {

	public final double defaultValue;
	public final Object2DoubleMap<MaterialType> materialTypes;
	public final Object2DoubleMap<IMaterial> materials;
	public final String path;
	public final String comment;
	public final Object2DoubleMap<IMaterial> configMaterials;

	private MaterialDoubleFunction(double defaultValue, Object2DoubleMap<MaterialType> materialTypes, Object2DoubleMap<IMaterial> materials, String path, String comment) {
		this.defaultValue = defaultValue;
		this.materialTypes = Objects.requireNonNull(materialTypes);
		this.materials = Objects.requireNonNull(materials);
		this.path = Strings.nullToEmpty(path);
		this.comment = Strings.nullToEmpty(comment);
		configMaterials = !path.isEmpty() ? new Object2DoubleOpenHashMap<>() : Object2DoubleMaps.emptyMap();
	}

	public static MaterialDoubleFunction of(double defaultValue, Object2DoubleMap<MaterialType> materialTypes, Object2DoubleMap<IMaterial> materials, String path, String comment) {
		return new MaterialDoubleFunction(defaultValue, materialTypes, materials, path, comment);
	}

	public static MaterialDoubleFunction of(double defaultValue, Object2DoubleMap<MaterialType> materialTypes, Object2DoubleMap<IMaterial> materials) {
		return of(defaultValue, materialTypes, materials, "", "");
	}

	public static MaterialDoubleFunction of(double defaultValue, String path, String comment) {
		return of(defaultValue, Object2DoubleMaps.emptyMap(), Object2DoubleMaps.emptyMap(), path, comment);
	}

	public static MaterialDoubleFunction of(double defaultValue) {
		return of(defaultValue, Object2DoubleMaps.emptyMap(), Object2DoubleMaps.emptyMap(), "", "");
	}

	public static MaterialDoubleFunction of(double defaultValue, ToDoubleFunction<IMaterial> function) {
		if(function instanceof MaterialDoubleFunction mf) {
			return of(defaultValue, mf.materialTypes, mf.materials, mf.path, mf.comment);
		}
		Object2DoubleMap<IMaterial> materialMap = JAOPCAApi.instance().getMaterials().stream().collect(
				Collectors.toMap(Function.identity(), function::applyAsDouble, (a, b)->a, Object2DoubleOpenHashMap::new));
		return of(defaultValue, Object2DoubleMaps.emptyMap(), materialMap, "", "");
	}

	@Override
	public double applyAsDouble(IMaterial material) {
		if(!path.isEmpty()) {
			return configMaterials.computeIfAbsent(material,
					m->JAOPCAApi.instance().getMaterialConfig(material).getDefinedDouble(path, applyAsDoubleUnconfigured(material), comment));
		}
		return applyAsDoubleUnconfigured(material);
	}

	public double applyAsDoubleUnconfigured(IMaterial material) {
		if(materials.containsKey(material)) {
			return materials.getDouble(material);
		}
		if(materialTypes.containsKey(material.getType())) {
			return materialTypes.getDouble(material.getType());
		}
		return defaultValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultValue, materialTypes, materials, path);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MaterialDoubleFunction other) {
			return defaultValue == other.defaultValue &&
					materialTypes.equals(other.materialTypes) &&
					materials.equals(other.materials) &&
					path.equals(other.path);
		}
		return false;
	}
}
