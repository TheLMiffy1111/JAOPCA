package thelm.jaopca.compat.mekanism.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.compat.mekanism.api.gases.IGasFormSettings;
import thelm.jaopca.compat.mekanism.gases.GasFormType;
import thelm.jaopca.utils.JsonHelper;

public class GasFormSettingsDeserializer implements JsonDeserializer<IGasFormSettings> {

	public static final GasFormSettingsDeserializer INSTANCE = new GasFormSettingsDeserializer();

	private GasFormSettingsDeserializer() {}

	public IGasFormSettings deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
		return deserialize(jsonElement, IGasFormSettings.class, context);
	}

	@Override
	public IGasFormSettings deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "settings");
		IGasFormSettings settings = GasFormType.INSTANCE.getNewSettings();
		if(json.has("isVisible")) {
			settings.setIsVisible(helper.getBoolean(json, "isVisible"));
		}
		return settings;
	}
}
