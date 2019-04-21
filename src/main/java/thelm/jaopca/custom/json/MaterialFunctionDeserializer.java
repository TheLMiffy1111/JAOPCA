package thelm.jaopca.custom.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.materials.EnumMaterialType;
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
			LOGGER.warn("Null default value: "+helper.toSimpleString(json.get("default")));
		}
		Object2ObjectMap<IMaterial, Object> map = new Object2ObjectRBTreeMap<>();
		map.defaultReturnValue(defaultValue);
		if(json.has("materialTypes")) {
			JsonObject materialTypesJson = helper.getJsonObject(json, "materialTypes");
			for(Map.Entry<String, JsonElement> entry : materialTypesJson.entrySet()) {
				Object materialTypeValue = helper.deserializeType(entry.getValue(), "element", context, parameterizedType);
				if(materialTypeValue == null) {
					LOGGER.warn("Null value for material type "+entry.getKey()+": "+helper.toSimpleString(entry.getValue()));
				}
				switch(entry.getKey()) {
				case "ingot":
					MaterialHandler.getMaterials().stream().
					filter(m->ArrayUtils.contains(EnumMaterialType.INGOTS, m.getType())).
					forEach(m->map.put(m, materialTypeValue));
					break;
				case "gem":
					MaterialHandler.getMaterials().stream().
					filter(m->ArrayUtils.contains(EnumMaterialType.GEMS, m.getType())).
					forEach(m->map.put(m, materialTypeValue));
					break;
				case "crystal":
					MaterialHandler.getMaterials().stream().
					filter(m->ArrayUtils.contains(EnumMaterialType.CRYSTALS, m.getType())).
					forEach(m->map.put(m, materialTypeValue));
					break;
				case "dust":
					MaterialHandler.getMaterials().stream().
					filter(m->ArrayUtils.contains(EnumMaterialType.DUSTS, m.getType())).
					forEach(m->map.put(m, materialTypeValue));
					break;
				}
			}
		}
		if(json.has("materials")) {
			JsonObject materialsJson = helper.getJsonObject(json, "materials");
			for(Map.Entry<String, JsonElement> entry : materialsJson.entrySet()) {
				if(MaterialHandler.containsMaterial(entry.getKey())) {
					Object materialValue = helper.deserializeType(entry.getValue(), "element", context, parameterizedType);
					if(materialValue == null) {
						LOGGER.warn("Null value for material "+entry.getKey()+": "+helper.toSimpleString(entry.getValue()));
					}
					map.put(MaterialHandler.getMaterial(entry.getKey()), materialValue);
				}
			}
		}
		return map;
	}
}
