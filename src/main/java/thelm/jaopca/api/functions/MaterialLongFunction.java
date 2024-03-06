package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;

public final class MaterialLongFunction implements ToLongFunction<IMaterial> {

	public final long defaultValue;
	public final Object2LongMap<MaterialType> materialTypes;
	public final Object2LongMap<IMaterial> materials;
	public final String path;
	public final String comment;
	public final Object2LongMap<IMaterial> configMaterials;

	private MaterialLongFunction(long defaultValue, Object2LongMap<MaterialType> materialTypes, Object2LongMap<IMaterial> materials, String path, String comment) {
		this.defaultValue = defaultValue;
		this.materialTypes = Objects.requireNonNull(materialTypes);
		this.materials = Objects.requireNonNull(materials);
		this.path = Strings.nullToEmpty(path);
		this.comment = Strings.nullToEmpty(comment);
		configMaterials = !path.isEmpty() ? new Object2LongOpenHashMap<>() : Object2LongMaps.emptyMap();
	}

	public static MaterialLongFunction of(long defaultValue, Object2LongMap<MaterialType> materialTypes, Object2LongMap<IMaterial> materials, String path, String comment) {
		return new MaterialLongFunction(defaultValue, materialTypes, materials, path, comment);
	}

	public static MaterialLongFunction of(long defaultValue, Object2LongMap<MaterialType> materialTypes, Object2LongMap<IMaterial> materials) {
		return of(defaultValue, materialTypes, materials, "", "");
	}

	public static MaterialLongFunction of(long defaultValue, String path, String comment) {
		return of(defaultValue, Object2LongMaps.emptyMap(), Object2LongMaps.emptyMap(), path, comment);
	}

	public static MaterialLongFunction of(long defaultValue) {
		return of(defaultValue, Object2LongMaps.emptyMap(), Object2LongMaps.emptyMap(), "", "");
	}

	public static MaterialLongFunction of(long defaultValue, ToLongFunction<IMaterial> function) {
		if(function instanceof MaterialLongFunction mf) {
			return of(defaultValue, mf.materialTypes, mf.materials, mf.path, mf.comment);
		}
		Object2LongMap<IMaterial> materialMap = JAOPCAApi.instance().getMaterials().stream().collect(
				Collectors.toMap(Function.identity(), function::applyAsLong, (a, b)->a, Object2LongOpenHashMap::new));
		return of(defaultValue, Object2LongMaps.emptyMap(), materialMap, "", "");
	}

	@Override
	public long applyAsLong(IMaterial material) {
		if(!path.isEmpty()) {
			return configMaterials.computeIfAbsent(material,
					m->JAOPCAApi.instance().getMaterialConfig(material).getDefinedLong(path, applyAsLongUnconfigured(material), comment));
		}
		return applyAsLongUnconfigured(material);
	}

	public long applyAsLongUnconfigured(IMaterial material) {
		if(materials.containsKey(material)) {
			return materials.getLong(material);
		}
		if(materialTypes.containsKey(material.getType())) {
			return materialTypes.getLong(material.getType());
		}
		return defaultValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultValue, materialTypes, materials, path);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MaterialLongFunction other) {
			return defaultValue == other.defaultValue &&
					materialTypes.equals(other.materialTypes) &&
					materials.equals(other.materials) &&
					path.equals(other.path);
		}
		return false;
	}
}
