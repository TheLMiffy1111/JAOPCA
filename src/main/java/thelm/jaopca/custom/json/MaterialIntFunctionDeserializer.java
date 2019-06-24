package thelm.jaopca.custom.json;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.ToIntFunction;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.CustomModule;
import thelm.jaopca.utils.JsonHelper;

public class MaterialIntFunctionDeserializer implements JsonDeserializer<ToIntFunction<IMaterial>> {

	public static final MaterialIntFunctionDeserializer INSTANCE = new MaterialIntFunctionDeserializer();

	private MaterialIntFunctionDeserializer() {}

	@Override
	public ToIntFunction<IMaterial> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "object");
		int defaultValue = helper.getInt(json, "default");
		Object2IntMap<IMaterial> map = new Object2IntRBTreeMap<>();
		map.defaultReturnValue(defaultValue);
		if(json.has("materialTypes")) {
			JsonObject materialTypesJson = helper.getJsonObject(json, "materialTypes");
			for(Map.Entry<String, JsonElement> entry : materialTypesJson.entrySet()) {
				int materialTypeValue = helper.getInt(entry.getValue(), "element");
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
			JsonObject materialsJson = helper.getJsonObject(json, "materials");
			for(Map.Entry<String, JsonElement> entry : materialsJson.entrySet()) {
				if(MaterialHandler.containsMaterial(entry.getKey())) {
					map.put(MaterialHandler.getMaterial(entry.getKey()), helper.getInt(entry.getValue(), "element"));
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
					map.put(material, config.getDefinedInt(path, map.getInt(material), comment));
				});
			}
		}
		return map;
	}
}
