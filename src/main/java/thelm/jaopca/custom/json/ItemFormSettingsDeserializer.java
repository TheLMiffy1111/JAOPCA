package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.world.item.Rarity;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.JsonHelper;

public class ItemFormSettingsDeserializer implements JsonDeserializer<IItemFormSettings> {

	public static final ItemFormSettingsDeserializer INSTANCE = new ItemFormSettingsDeserializer();

	private ItemFormSettingsDeserializer() {}

	public IItemFormSettings deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
		return deserialize(jsonElement, IItemFormSettings.class, context);
	}

	@Override
	public IItemFormSettings deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "settings");
		IItemFormSettings settings = ItemFormType.INSTANCE.getNewSettings();
		if(json.has("itemStackLimit")) {
			JsonObject functionJson = helper.getJsonObject(json, "itemStackLimit");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 64);
			}
			settings.setItemStackLimitFunction(helper.deserializeType(json, "itemStackLimit", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("hasEffect")) {
			boolean hasEffect = helper.getBoolean(json, "hasEffect");
			settings.setHasEffectFunction(m->m.hasEffect() || hasEffect);
		}
		if(json.has("rarity")) {
			Rarity rarity = helper.deserializeType(json, "rarity", context, Rarity.class);
			settings.setDisplayRarityFunction(m->rarity);
		}
		if(json.has("burnTime")) {
			JsonObject functionJson = helper.getJsonObject(json, "burnTime");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", -1);
			}
			settings.setBurnTimeFunction(helper.deserializeType(json, "burnTime", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		return settings;
	}
}
