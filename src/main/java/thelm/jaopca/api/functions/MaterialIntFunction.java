package thelm.jaopca.api.functions;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;

public final class MaterialIntFunction implements ToIntFunction<IMaterial> {

	public final int defaultValue;
	public final Object2IntMap<MaterialType> materialTypes;
	public final Object2IntMap<IMaterial> materials;
	public final String path;
	public final String comment;
	public final Object2IntMap<IMaterial> configMaterials;

	private MaterialIntFunction(int defaultValue, Object2IntMap<MaterialType> materialTypes, Object2IntMap<IMaterial> materials, String path, String comment) {
		this.defaultValue = defaultValue;
		this.materialTypes = Objects.requireNonNull(materialTypes);
		this.materials = Objects.requireNonNull(materials);
		this.path = Strings.nullToEmpty(path);
		this.comment = Strings.nullToEmpty(comment);
		configMaterials = !path.isEmpty() ? new Object2IntOpenHashMap<>() : Object2IntMaps.emptyMap();
	}

	public static MaterialIntFunction of(int defaultValue, Object2IntMap<MaterialType> materialTypes, Object2IntMap<IMaterial> materials, String path, String comment) {
		return new MaterialIntFunction(defaultValue, materialTypes, materials, path, comment);
	}

	public static MaterialIntFunction of(int defaultValue, Object2IntMap<MaterialType> materialTypes, Object2IntMap<IMaterial> materials) {
		return of(defaultValue, materialTypes, materials, "", "");
	}

	public static MaterialIntFunction of(int defaultValue, String path, String comment) {
		return of(defaultValue, Object2IntMaps.emptyMap(), Object2IntMaps.emptyMap(), path, comment);
	}

	public static MaterialIntFunction of(int defaultValue) {
		return of(defaultValue, Object2IntMaps.emptyMap(), Object2IntMaps.emptyMap(), "", "");
	}

	public static MaterialIntFunction of(int defaultValue, ToIntFunction<IMaterial> function) {
		if(function instanceof MaterialIntFunction mf) {
			return of(defaultValue, mf.materialTypes, mf.materials, mf.path, mf.comment);
		}
		Object2IntMap<IMaterial> materialMap = JAOPCAApi.instance().getMaterials().stream().collect(
				Collectors.toMap(Function.identity(), function::applyAsInt, (a, b)->a, Object2IntOpenHashMap::new));
		return of(defaultValue, Object2IntMaps.emptyMap(), materialMap, "", "");
	}

	@Override
	public int applyAsInt(IMaterial material) {
		if(!path.isEmpty()) {
			return configMaterials.computeIfAbsent(material,
					m->JAOPCAApi.instance().getMaterialConfig(material).getDefinedInt(path, applyAsIntUnconfigured(material), comment));
		}
		return applyAsIntUnconfigured(material);
	}

	public int applyAsIntUnconfigured(IMaterial material) {
		if(materials.containsKey(material)) {
			return materials.getInt(material);
		}
		if(materialTypes.containsKey(material.getType())) {
			return materialTypes.getInt(material.getType());
		}
		return defaultValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultValue, materialTypes, materials, path);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MaterialIntFunction other) {
			return defaultValue == other.defaultValue &&
					materialTypes.equals(other.materialTypes) &&
					materials.equals(other.materials) &&
					path.equals(other.path);
		}
		return false;
	}
}
