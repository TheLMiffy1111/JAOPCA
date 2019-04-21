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
import thelm.jaopca.api.materials.EnumMaterialType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleCustom;
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
			for(Enum<?> value : ((Class<Enum<?>>)parameterizedType).getEnumConstants()) {
				stringToEnum.put(value.name().toLowerCase(Locale.US), value);
				enumToString.put(value, value.name().toLowerCase(Locale.US));
			}
			JsonObject json = helper.getJsonObject(jsonElement, "object");
			String defaultString = helper.getString(json, "default");
			Enum<?> defaultValue = stringToEnum.get(defaultString.toLowerCase(Locale.US));
			if(defaultValue == null) {
				throw new JsonSyntaxException("Invalid enum "+defaultString);
			}
			Object2ObjectMap<IMaterial, Enum<?>> map = new Object2ObjectRBTreeMap<>();
			map.defaultReturnValue(defaultValue);
			if(json.has("materialTypes")) {
				JsonObject materialTypesJson = helper.getJsonObject(json, "materialTypes");
				for(Map.Entry<String, JsonElement> entry : materialTypesJson.entrySet()) {
					String materialTypeString = helper.getString(entry.getValue(), "element");
					Enum<?> materialTypeValue = stringToEnum.get(materialTypeString.toLowerCase(Locale.US));
					if(materialTypeValue == null) {
						throw new JsonSyntaxException("Invalid enum "+materialTypeString);
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
						String materialString = helper.getString(entry.getValue(), "element");
						Enum<?> materialValue = stringToEnum.get(materialString.toLowerCase(Locale.US));
						if(materialValue == null) {
							throw new JsonSyntaxException("Invalid enum "+materialString);
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
					ModuleCustom.instance.addCustomConfigDefiner((material, config)->{
						Enum<?> value = stringToEnum.get(config.getDefinedString(path, enumToString.get(map.get(material)), comment).toLowerCase(Locale.US));
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
