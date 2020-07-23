package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.item.Rarity;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.fluids.FluidFormType;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.JsonHelper;

public class FluidFormSettingsDeserializer implements JsonDeserializer<IFluidFormSettings> {

	public static final FluidFormSettingsDeserializer INSTANCE = new FluidFormSettingsDeserializer();

	private FluidFormSettingsDeserializer() {}

	public IFluidFormSettings deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
		return deserialize(jsonElement, IFluidFormSettings.class, context);
	}

	@Override
	public IFluidFormSettings deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "settings");
		IFluidFormSettings settings = FluidFormType.INSTANCE.getNewSettings();
		if(json.has("maxLevel")) {
			JsonObject functionJson = helper.getJsonObject(json, "maxLevel");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 8);
			}
			settings.setMaxLevelFunction(helper.deserializeType(json, "maxLevel", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("tickRate")) {
			JsonObject functionJson = helper.getJsonObject(json, "tickRate");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 5);
			}
			settings.setTickRateFunction(helper.deserializeType(json, "tickRate", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("explosionResistance")) {
			JsonObject functionJson = helper.getJsonObject(json, "explosionResistance");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 100);
			}
			settings.setExplosionResistanceFunction(helper.deserializeType(json, "explosionResistance", context, FormTypeHandler.DOUBLE_FUNCTION_TYPE));
		}
		if(json.has("canSourcesMultiply")) {
			JsonObject functionJson = helper.getJsonObject(json, "canSourcesMultiply");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", false);
			}
			settings.setCanSourcesMultiplyFunction(helper.deserializeType(json, "canSourcesMultiply", context, FormTypeHandler.PREDICATE_TYPE));
		}
		if(json.has("blockMaterial")) {
			JsonObject functionJson = helper.getJsonObject(json, "blockMaterial");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "iron");
			}
			settings.setMaterialFunction(helper.deserializeType(json, "blockMaterial", context, BlockFormType.MATERIAL_FUNCTION_TYPE));
		}
		if(json.has("fillSound")) {
			settings.setFillSoundSupplier(helper.deserializeType(json, "fillSound", context, FluidFormType.SOUND_EVENT_SUPPLIER_TYPE));
		}
		if(json.has("emptySound")) {
			settings.setFillSoundSupplier(helper.deserializeType(json, "emptySound", context, FluidFormType.SOUND_EVENT_SUPPLIER_TYPE));
		}
		if(json.has("density")) {
			JsonObject functionJson = helper.getJsonObject(json, "density");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 1000);
			}
			settings.setDensityFunction(helper.deserializeType(json, "density", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("viscosity")) {
			JsonObject functionJson = helper.getJsonObject(json, "viscosity");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 1000);
			}
			settings.setViscosityFunction(helper.deserializeType(json, "viscosity", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("temperature")) {
			JsonObject functionJson = helper.getJsonObject(json, "temperature");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 300);
			}
			settings.setTemperatureFunction(helper.deserializeType(json, "temperature", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("rarity")) {
			Rarity rarity = helper.deserializeType(json, "rarity", context, Rarity.class);
			settings.setDisplayRarityFunction(m->rarity);
		}
		if(json.has("levelDecreasePerBlock")) {
			JsonObject functionJson = helper.getJsonObject(json, "levelDecreasePerBlock");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 1);
			}
			settings.setLevelDecreasePerBlockFunction(helper.deserializeType(json, "levelDecreasePerBlock", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("blockMaterial")) {
			JsonObject functionJson = helper.getJsonObject(json, "blockMaterial");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "water");
			}
			settings.setMaterialFunction(helper.deserializeType(json, "blockMaterial", context, BlockFormType.MATERIAL_FUNCTION_TYPE));
		}
		if(json.has("lightValue")) {
			JsonObject functionJson = helper.getJsonObject(json, "lightValue");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setLightValueFunction(helper.deserializeType(json, "lightValue", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("blockHardness")) {
			JsonObject functionJson = helper.getJsonObject(json, "blockHardness");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 5);
			}
			settings.setBlockHardnessFunction(helper.deserializeType(json, "blockHardness", context, FormTypeHandler.DOUBLE_FUNCTION_TYPE));
		}
		if(json.has("flammability")) {
			JsonObject functionJson = helper.getJsonObject(json, "flammability");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setFlammabilityFunction(helper.deserializeType(json, "flammability", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("fireSpreadSpeed")) {
			JsonObject functionJson = helper.getJsonObject(json, "fireSpreadSpeed");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setFireSpreadSpeedFunction(helper.deserializeType(json, "fireSpreadSpeed", context, FormTypeHandler.INT_FUNCTION_TYPE));
		}
		if(json.has("isFireSource")) {
			JsonObject functionJson = helper.getJsonObject(json, "isFireSource");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", false);
			}
			settings.setIsFireSourceFunction(helper.deserializeType(json, "isFireSource", context, FormTypeHandler.PREDICATE_TYPE));
		}
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
