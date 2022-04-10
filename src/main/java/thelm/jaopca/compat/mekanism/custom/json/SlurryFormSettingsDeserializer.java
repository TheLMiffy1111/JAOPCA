package thelm.jaopca.compat.mekanism.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormSettings;
import thelm.jaopca.compat.mekanism.slurries.SlurryFormType;
import thelm.jaopca.utils.JsonHelper;

public class SlurryFormSettingsDeserializer implements JsonDeserializer<ISlurryFormSettings> {

	public static final SlurryFormSettingsDeserializer INSTANCE = new SlurryFormSettingsDeserializer();

	private SlurryFormSettingsDeserializer() {}

	public ISlurryFormSettings deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
		return deserialize(jsonElement, ISlurryFormSettings.class, context);
	}

	@Override
	public ISlurryFormSettings deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "settings");
		ISlurryFormSettings settings = SlurryFormType.INSTANCE.getNewSettings();
		if(json.has("isHidden")) {
			settings.setIsHidden(helper.getBoolean(json, "isHidden"));
		}
		return settings;
	}
}
