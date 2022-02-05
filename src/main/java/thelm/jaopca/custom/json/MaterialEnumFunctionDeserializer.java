package thelm.jaopca.custom.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.utils.JsonHelper;

public class MaterialEnumFunctionDeserializer implements JsonDeserializer<Function<IMaterial, Enum<?>>> {

	public static final MaterialEnumFunctionDeserializer INSTANCE = new MaterialEnumFunctionDeserializer();

	private MaterialEnumFunctionDeserializer() {}

	@Override
	public Function<IMaterial, Enum<?>> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		Type[] typeArguments = ((ParameterizedType)typeOfT).getActualTypeArguments();
		Type parameterizedType = typeArguments[1];
		if(parameterizedType instanceof Class && ((Class<?>)parameterizedType).isEnum()) {
			Map<String, Enum<?>> stringToEnum = new TreeMap<>();
			Map<Enum<?>, String> enumToString = new TreeMap<>();
			Enum<?>[] values = ((Class<Enum<?>>)parameterizedType).getEnumConstants();
			for(Enum<?> value : ((Class<Enum<?>>)parameterizedType).getEnumConstants()) {
				stringToEnum.put(value.name().toLowerCase(Locale.US), value);
				enumToString.put(value, value.name());
			}
			JsonObject json = helper.getJsonObject(jsonElement, "object");
			Enum<?> defaultValue = null;
			if(helper.isString(json, "default")) {
				String defaultString = helper.getString(json, "default");
				Enum<?> value = stringToEnum.get(defaultString.toLowerCase(Locale.US));
				if(value == null) {
					throw new JsonSyntaxException("Invalid enum "+defaultString);
				}
				defaultValue = value;
			}
			else if(helper.isNumber(json, "default")) {
				int defaultOrdinal = helper.getInt(json, "default");
				if(defaultOrdinal >= values.length) {
					throw new JsonSyntaxException("Invalid enum ordinal "+defaultOrdinal);
				}
				defaultValue = values[defaultOrdinal];
			}
			else {
				throw new JsonSyntaxException("Unable to deserialize enum");
			}
			Object2ObjectMap<IMaterial, Enum<?>> map = new Object2ObjectRBTreeMap<>();
			map.defaultReturnValue(defaultValue);
			if(json.has("materialTypes")) {
				JsonObject materialTypesJson = helper.getJsonObject(json, "materialTypes");
				for(Map.Entry<String, JsonElement> entry : materialTypesJson.entrySet()) {
					Enum<?> materialTypeValue;
					if(helper.isString(entry.getValue())) {
						String materialTypeString = helper.getString(entry.getValue(), "element");
						Enum<?> value = stringToEnum.get(materialTypeString.toLowerCase(Locale.US));
						if(value == null) {
							throw new JsonSyntaxException("Invalid enum "+materialTypeString);
						}
						materialTypeValue = value;
					}
					else if(helper.isNumber(entry.getValue())) {
						int materialTypeOrdinal = helper.getInt(entry.getValue(), "element");
						if(materialTypeOrdinal >= values.length) {
							throw new JsonSyntaxException("Invalid enum ordinal "+materialTypeOrdinal);
						}
						materialTypeValue = values[materialTypeOrdinal];
					}
					else {
						throw new JsonSyntaxException("Unable to deserialize enum");
					}
					switch(entry.getKey()) {
					case "ingot":
						MaterialHandler.getMaterials().stream().
						filter(m->m.getType().isIngot()).
						forEach(m->map.put(m, materialTypeValue));
						break;
					case "gem":
						MaterialHandler.getMaterials().stream().
						filter(m->m.getType().isGem()).
						forEach(m->map.put(m, materialTypeValue));
						break;
					case "crystal":
						MaterialHandler.getMaterials().stream().
						filter(m->m.getType().isCrystal()).
						forEach(m->map.put(m, materialTypeValue));
						break;
					case "dust":
						MaterialHandler.getMaterials().stream().
						filter(m->m.getType().isDust()).
						forEach(m->map.put(m, materialTypeValue));
						break;
					}
				}
			}
			if(json.has("materials")) {
				JsonObject materialsJson = helper.getJsonObject(json, "materials");
				for(Map.Entry<String, JsonElement> entry : materialsJson.entrySet()) {
					if(MaterialHandler.containsMaterial(entry.getKey())) {
						Enum<?> materialValue;
						if(helper.isString(entry.getValue())) {
							String materialString = helper.getString(entry.getValue(), "element");
							Enum<?> value = stringToEnum.get(materialString.toLowerCase(Locale.US));
							if(value == null) {
								throw new JsonSyntaxException("Invalid enum "+materialString);
							}
							materialValue = value;
						}
						else if(helper.isNumber(entry.getValue())) {
							int materialOrdinal = helper.getInt(entry.getValue(), "element");
							if(materialOrdinal >= values.length) {
								throw new JsonSyntaxException("Invalid enum ordinal "+materialOrdinal);
							}
							materialValue = values[materialOrdinal];
						}
						else {
							throw new JsonSyntaxException("Unable to deserialize enum");
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
						Enum<?> value = config.getDefinedEnum(path, (Class<Enum>)parameterizedType, (Enum)map.get(material), comment);
						if(value != null) {
							map.put(material, value);
						}
					});
				}
			}
			return map;
		}
		throw new JsonParseException("Unable to deserialize "+helper.toSimpleString(jsonElement)+" into an enum function");
	}
}
