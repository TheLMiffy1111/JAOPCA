package thelm.jaopca.custom.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.utils.JsonHelper;

public class MaterialFunctionDeserializer implements JsonDeserializer<Function<IMaterial, ?>> {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final MaterialFunctionDeserializer INSTANCE = new MaterialFunctionDeserializer();

	private MaterialFunctionDeserializer() {}

	@Override
	public Function<IMaterial, ?> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		Type[] typeArguments = ((ParameterizedType)typeOfT).getActualTypeArguments();
		Type parameterizedType = typeArguments[1];
		JsonObject json = helper.getJsonObject(jsonElement, "object");
		Object defaultValue = helper.deserializeType(json, "default", context, parameterizedType);
		if(defaultValue == null) {
			LOGGER.warn("Null default value: {}", new Object[] {helper.toSimpleString(json.get("default"))});
		}
		Map<IMaterial, Object> map = new TreeMap<>();
		if(json.has("materialTypes")) {
			JsonObject materialTypesJson = helper.getJsonObject(json, "materialTypes");
			for(Map.Entry<String, JsonElement> entry : materialTypesJson.entrySet()) {
				Object materialTypeValue = helper.deserializeType(entry.getValue(), "element", context, parameterizedType);
				if(materialTypeValue == null) {
					LOGGER.warn("Null value for material type {}: {}", new Object[] {entry.getKey(), helper.toSimpleString(entry.getValue())});
				}
				switch(entry.getKey()) {
				case "ingot":
					MaterialHandler.getMaterials().stream().
					filter(m->m.getType().isIngot()).
					forEach(m->map.put(m, materialTypeValue));
				case "gem":
					MaterialHandler.getMaterials().stream().
					filter(m->m.getType().isGem()).
					forEach(m->map.put(m, materialTypeValue));
				case "crystal":
					MaterialHandler.getMaterials().stream().
					filter(m->m.getType().isCrystal()).
					forEach(m->map.put(m, materialTypeValue));
				case "dust":
					MaterialHandler.getMaterials().stream().
					filter(m->m.getType().isDust()).
					forEach(m->map.put(m, materialTypeValue));
				}
			}
		}
		if(json.has("materials")) {
			JsonObject materialsJson = helper.getJsonObject(json, "materials");
			for(Map.Entry<String, JsonElement> entry : materialsJson.entrySet()) {
				if(MaterialHandler.containsMaterial(entry.getKey())) {
					Object materialValue = helper.deserializeType(entry.getValue(), "element", context, parameterizedType);
					if(materialValue == null) {
						LOGGER.warn("Null value for material {}: {}", new Object[] {entry.getKey(), helper.toSimpleString(entry.getValue())});
					}
					map.put(MaterialHandler.getMaterial(entry.getKey()), materialValue);
				}
			}
		}
		return material->map.getOrDefault(material, defaultValue);
	}
}
