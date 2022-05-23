package thelm.jaopca.custom.json;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

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
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.utils.JsonHelper;

public class MaterialMappedFunctionDeserializer<T> implements JsonDeserializer<Function<IMaterial, T>> {

	private static final Logger LOGGER = LogManager.getLogger();

	private final Function<String, T> stringToValue;
	private final Function<T, String> valueToString;

	public MaterialMappedFunctionDeserializer(Function<String, T> stringToValue, Function<T, String> valueToString) {
		this.stringToValue = Objects.requireNonNull(stringToValue);
		this.valueToString = Objects.requireNonNull(valueToString);
	}

	@Override
	public Function<IMaterial, T> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "object");
		String defaultString = helper.getString(json, "default");
		T defaultValue = stringToValue.apply(defaultString);
		if(defaultValue == null) {
			LOGGER.warn("Null default value: {}", defaultString);
		}
		Object2ObjectMap<IMaterial, T> map = new Object2ObjectRBTreeMap<>();
		map.defaultReturnValue(defaultValue);
		if(json.has("materialTypes")) {
			JsonObject materialTypesJson = helper.getJsonObject(json, "materialTypes");
			for(Map.Entry<String, JsonElement> entry : materialTypesJson.entrySet()) {
				String materialTypeString = helper.getString(entry.getValue(), "element");
				T materialTypeValue = stringToValue.apply(materialTypeString.toLowerCase(Locale.US));
				if(materialTypeValue == null) {
					LOGGER.warn("Null value for material type {}: {}", entry.getKey(), materialTypeString);
				}
				switch(entry.getKey()) {
				case "ingot" ->
					MaterialHandler.getMaterials().stream().
					filter(m->m.getType().isIngot()).
					forEach(m->map.put(m, materialTypeValue));
				case "gem" ->
					MaterialHandler.getMaterials().stream().
					filter(m->m.getType().isGem()).
					forEach(m->map.put(m, materialTypeValue));
				case "crystal" ->
					MaterialHandler.getMaterials().stream().
					filter(m->m.getType().isCrystal()).
					forEach(m->map.put(m, materialTypeValue));
				case "dust" ->
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
					String materialString = helper.getString(entry.getValue(), "element");
					T materialValue = stringToValue.apply(materialString);
					if(materialValue == null) {
						LOGGER.warn("Null value for material {}: {}", entry.getKey(), materialString);
					}
					map.put(MaterialHandler.getMaterial(entry.getKey()), materialValue);
				}
			}
		}
		if(json.has("config")) {
			if(helper.getBoolean(json, "config")) {
				String path = helper.getString(json, "path");
				String comment;
				if(json.has("comment")) {
					comment = helper.getString(json, "comment");
				}
				else {
					comment = "";
				}
				CustomModule.instance.addCustomConfigDefiner((material, config)->{
					T value = stringToValue.apply(config.getDefinedString(path, ""+valueToString.apply(map.get(material)), comment));
					if(value == null) {
						LOGGER.warn("Null config value for material {}", material.getName());
					}
					map.put(material, value);
				});
			}
		}
		return map;
	}
}
