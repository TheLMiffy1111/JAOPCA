package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.JsonHelper;

public class BlockFormSettingsDeserializer implements JsonDeserializer<IBlockFormSettings> {

	public static final BlockFormSettingsDeserializer INSTANCE = new BlockFormSettingsDeserializer();

	private BlockFormSettingsDeserializer() {}

	public IBlockFormSettings deserialize(JsonElement jsonElement, JsonDeserializationContext context) {
		return deserialize(jsonElement, IBlockFormSettings.class, context);
	}

	@Override
	public IBlockFormSettings deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "settings");
		IBlockFormSettings settings = BlockFormType.INSTANCE.getNewSettings();
		if(json.has("mapColor")) {
			JsonObject functionJson = helper.getJsonObject(json, "mapColor");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "metal");
			}
			settings.setMapColorFunction(helper.deserializeType(json, "mapColor", context, BlockFormType.MAP_COLOR_FUNCTION_TYPE));
		}
		if(json.has("replaceable")) {
			settings.setReplaceable(helper.getBoolean(json, "replaceable"));
		}
		if(json.has("soundType")) {
			JsonObject functionJson = helper.getJsonObject(json, "soundType");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "metal");
			}
			settings.setSoundTypeFunction(helper.deserializeType(json, "soundType", context, BlockFormType.SOUND_TYPE_FUNCTION_TYPE));
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
		if(json.has("explosionResistance")) {
			JsonObject functionJson = helper.getJsonObject(json, "explosionResistance");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 6);
			}
			settings.setExplosionResistanceFunction(helper.deserializeType(json, "explosionResistance", context, FormTypeHandler.DOUBLE_FUNCTION_TYPE));
		}
		if(json.has("friction")) {
			JsonObject functionJson = helper.getJsonObject(json, "friction");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0.6);
			}
			settings.setFrictionFunction(helper.deserializeType(json, "friction", context, FormTypeHandler.DOUBLE_FUNCTION_TYPE));
		}
		if(json.has("shape")) {
			settings.setShape(helper.deserializeType(json, "shape", context, VoxelShape.class));
		}
		if(json.has("interactionShape")) {
			settings.setInteractionShape(helper.deserializeType(json, "interactionShape", context, VoxelShape.class));
		}
		if(json.has("requiresTool")) {
			JsonObject functionJson = helper.getJsonObject(json, "requiresTool");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", false);
			}
			settings.setRequiresToolFunction(helper.deserializeType(json, "requiresTool", context, FormTypeHandler.PREDICATE_TYPE));
		}
		if(json.has("harvestToolTag")) {
			JsonObject functionJson = helper.getJsonObject(json, "harvestToolTag");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "minecraft:mineable/pickaxe");
			}
			settings.setHarvestToolTagFunction(helper.deserializeType(json, "harvestToolTag", context, FormTypeHandler.STRING_FUNCTION_TYPE));
		}
		if(json.has("harvestTierTag")) {
			JsonObject functionJson = helper.getJsonObject(json, "harvestTierTag");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "");
			}
			settings.setHarvestTierTagFunction(helper.deserializeType(json, "harvestTierTag", context, FormTypeHandler.STRING_FUNCTION_TYPE));
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
		if(json.has("pushReaction")) {
			PushReaction pushReaction = helper.deserializeType(json, "pushReaction", context, PushReaction.class);
			settings.setPushReactionFunction(m->pushReaction);
		}
		if(json.has("instrument")) {
			JsonObject functionJson = helper.getJsonObject(json, "instrument");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "harp");
			}
			settings.setInstrumentFunction(helper.deserializeType(json, "instrument", context, BlockFormType.INSTRUMENT_FUNCTION_TYPE));
		}
		if(json.has("maxStackSize")) {
			JsonObject functionJson = helper.getJsonObject(json, "maxStackSize");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 64);
			}
			settings.setMaxStackSizeFunction(helper.deserializeType(json, "maxStackSize", context, FormTypeHandler.INT_FUNCTION_TYPE));
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
