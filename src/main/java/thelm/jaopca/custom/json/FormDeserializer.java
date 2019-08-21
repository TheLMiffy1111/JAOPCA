package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.forms.Form;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.modules.CustomModule;
import thelm.jaopca.utils.JsonHelper;

public class FormDeserializer implements JsonDeserializer<IForm> {

	public static final FormDeserializer INSTANCE = new FormDeserializer();

	private FormDeserializer() {}

	@Override
	public IForm deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "element");
		String name = helper.getString(json, "name");
		IFormType type = FormTypeHandler.getFormType(helper.getString(json, "type"));
		IForm form = new Form(CustomModule.instance, name, type);
		if(json.has("secondaryName")) {
			form.setSecondaryName(helper.getString(json, "secondaryName"));
		}
		if(json.has("translationKey")) {
			form.setTranslationKey(helper.getString(json, "translationKey"));
		}
		if(json.has("materialTypes")) {
			form.setMaterialTypes(helper.<MaterialType[]>deserializeType(json, "materialTypes", context, MaterialType[].class));
		}
		if(json.has("defaultMaterialBlacklist")) {
			form.setDefaultMaterialBlacklist(helper.<String[]>deserializeType(json, "defaultMaterialBlacklist", context, String[].class));
		}
		if(json.has("skipGroupedCheck")) {
			form.setSkipGroupedCheck(helper.getBoolean(json, "skipGroupedCheck"));
		}
		if(json.has("settings")) {
			form.setSettings(type.deserializeSettings(json.get("settings"), context));
		}
		return form;
	}
}
