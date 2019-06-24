package thelm.jaopca.custom.json;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.ToLongFunction;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongRBTreeMap;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.CustomModule;
import thelm.jaopca.utils.JsonHelper;

public class MaterialLongFunctionDeserializer implements JsonDeserializer<ToLongFunction<IMaterial>> {

	public static final MaterialLongFunctionDeserializer INSTANCE = new MaterialLongFunctionDeserializer();

	private MaterialLongFunctionDeserializer() {}

	@Override
	public ToLongFunction<IMaterial> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "object");
		long defaultValue = helper.getLong(json, "default");
		Object2LongMap<IMaterial> map = new Object2LongRBTreeMap<>();
		map.defaultReturnValue(defaultValue);
		if(json.has("materialTypes")) {
			JsonObject materialTypesJson = helper.getJsonObject(json, "materialTypes");
			for(Map.Entry<String, JsonElement> entry : materialTypesJson.entrySet()) {
				long materialTypeValue = helper.getLong(entry.getValue(), "element");
				switch(entry.getKey()) {
				case "ingot":
					MaterialHandler.getMaterials().stream().
					filter(m->ArrayUtils.contains(MaterialType.INGOTS, m.getType())).
					forEach(m->map.put(m, materialTypeValue));
					break;
				case "gem":
					MaterialHandler.getMaterials().stream().
					filter(m->ArrayUtils.contains(MaterialType.GEMS, m.getType())).
					forEach(m->map.put(m, materialTypeValue));
					break;
				case "crystal":
					MaterialHandler.getMaterials().stream().
					filter(m->ArrayUtils.contains(MaterialType.CRYSTALS, m.getType())).
					forEach(m->map.put(m, materialTypeValue));
					break;
				case "dust":
					MaterialHandler.getMaterials().stream().
					filter(m->ArrayUtils.contains(MaterialType.DUSTS, m.getType())).
					forEach(m->map.put(m, materialTypeValue));
					break;
				}
			}
		}
		if(json.has("materials")) {
			JsonObject materialsJson = json.get("materials").getAsJsonObject();
			for(Map.Entry<String, JsonElement> entry : materialsJson.entrySet()) {
				if(MaterialHandler.containsMaterial(entry.getKey())) {
					map.put(MaterialHandler.getMaterial(entry.getKey()), helper.getLong(entry.getValue(), "element"));
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
					map.put(material, config.getDefinedLong(path, map.getLong(material), comment));
				});
			}
		}
		return map;
	}
}
