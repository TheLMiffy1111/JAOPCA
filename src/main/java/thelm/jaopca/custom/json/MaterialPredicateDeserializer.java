package thelm.jaopca.custom.json;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanRBTreeMap;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.custom.CustomModule;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.utils.JsonHelper;

public class MaterialPredicateDeserializer implements JsonDeserializer<Predicate<IMaterial>> {

	public static final MaterialPredicateDeserializer INSTANCE = new MaterialPredicateDeserializer();

	private MaterialPredicateDeserializer() {}

	@Override
	public Predicate<IMaterial> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "object");
		boolean defaultValue = helper.getBoolean(json, "default");
		Object2BooleanMap<IMaterial> map = new Object2BooleanRBTreeMap<>();
		map.defaultReturnValue(defaultValue);
		if(json.has("materialTypes")) {
			JsonObject materialTypesJson = helper.getJsonObject(json, "materialTypes");
			for(Map.Entry<String, JsonElement> entry : materialTypesJson.entrySet()) {
				boolean materialTypeValue = helper.getBoolean(entry.getValue(), "element");
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
					map.put(MaterialHandler.getMaterial(entry.getKey()), helper.getBoolean(entry.getValue(), "element"));
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
					map.put(material, config.getDefinedBoolean(path, map.getBoolean(material), comment));
				});
			}
		}
		return map;
	}
}
