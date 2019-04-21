package thelm.jaopca.custom.json;

import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.blocks.BlockFormType;
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
		Type intFunctionType = new TypeToken<ToIntFunction<IMaterial>>(){}.getType();
		Type doubleFunctionType = new TypeToken<ToDoubleFunction<IMaterial>>(){}.getType();
		Type predicateType = new TypeToken<Predicate<IMaterial>>(){}.getType();
		Type materialFunctionType = new TypeToken<Function<IMaterial, Material>>(){}.getType();
		Type soundTypeFunctionType = new TypeToken<Function<IMaterial, SoundType>>(){}.getType();
		Type toolTypeFunctionType = new TypeToken<Function<IMaterial, ToolType>>(){}.getType();
		IBlockFormSettings settings = BlockFormType.INSTANCE.getNewSettings();
		if(json.has("blockMaterial")) {
			JsonObject functionJson = helper.getJsonObject(json, "blockMaterial");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "iron");
			}
			settings.setMaterialFunction(helper.deserializeType(json, "blockMaterial", context, materialFunctionType));
		}
		if(json.has("soundType")) {
			JsonObject functionJson = helper.getJsonObject(json, "soundType");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "metal");
			}
			settings.setMaterialFunction(helper.deserializeType(json, "soundType", context, materialFunctionType));
		}
		if(json.has("lightValue")) {
			JsonObject functionJson = helper.getJsonObject(json, "lightValue");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setLightValueFunction(helper.deserializeType(json, "lightValue", context, intFunctionType));
		}
		if(json.has("blockHardness")) {
			JsonObject functionJson = helper.getJsonObject(json, "blockHardness");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 5);
			}
			settings.setBlockHardnessFunction(helper.deserializeType(json, "blockHardness", context, doubleFunctionType));
		}
		if(json.has("explosionResistance")) {
			JsonObject functionJson = helper.getJsonObject(json, "explosionResistance");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 6);
			}
			settings.setExplosionResistanceFunction(helper.deserializeType(json, "explosionResistance", context, doubleFunctionType));
		}
		if(json.has("slipperiness")) {
			JsonObject functionJson = helper.getJsonObject(json, "slipperiness");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0.6);
			}
			settings.setSlipperinessFunction(helper.deserializeType(json, "slipperiness", context, doubleFunctionType));
		}
		if(json.has("isFull")) {
			settings.setIsFull(helper.getBoolean(json, "isFull"));
		}
		if(json.has("shape")) {
			settings.setShape(helper.deserializeType(json, "shape", context, VoxelShape.class));
		}
		if(json.has("raytraceShape")) {
			settings.setRaytraceShape(helper.deserializeType(json, "raytraceShape", context, VoxelShape.class));
		}
		if(json.has("renderLayer")) {
			settings.setRenderLayer(helper.deserializeType(json, "renderLayer", context, BlockRenderLayer.class));
		}
		if(json.has("harvestTool")) {
			JsonObject functionJson = helper.getJsonObject(json, "harvestTool");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", "pickaxe");
			}
			settings.setHarvestToolFunction(helper.deserializeType(json, "harvestTool", context, toolTypeFunctionType));
		}
		if(json.has("harvestLevel")) {
			JsonObject functionJson = helper.getJsonObject(json, "harvestLevel");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setItemStackLimitFunction(helper.deserializeType(json, "harvestLevel", context, intFunctionType));
		}
		if(json.has("isBeaconBase")) {
			JsonObject functionJson = helper.getJsonObject(json, "isBeaconBase");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", false);
			}
			settings.setIsBeaconBaseFunction(helper.deserializeType(json, "isBeaconBase", context, predicateType));
		}
		if(json.has("flammability")) {
			JsonObject functionJson = helper.getJsonObject(json, "flammability");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setFlammabilityFunction(helper.deserializeType(json, "flammability", context, intFunctionType));
		}
		if(json.has("fireSpreadSpeed")) {
			JsonObject functionJson = helper.getJsonObject(json, "fireSpreadSpeed");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 0);
			}
			settings.setFireSpreadSpeedFunction(helper.deserializeType(json, "fireSpreadSpeed", context, intFunctionType));
		}
		if(json.has("isFireSource")) {
			JsonObject functionJson = helper.getJsonObject(json, "isFireSource");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", false);
			}
			settings.setIsFireSourceFunction(helper.deserializeType(json, "isFireSource", context, predicateType));
		}

		if(json.has("itemStackLimit")) {
			JsonObject functionJson = helper.getJsonObject(json, "itemStackLimit");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", 64);
			}
			settings.setItemStackLimitFunction(helper.deserializeType(json, "itemStackLimit", context, intFunctionType));
		}
		if(json.has("isBeaconPayment")) {
			JsonObject functionJson = helper.getJsonObject(json, "itemStackLimit");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", false);
			}
			settings.setIsBeaconPaymentFunction(helper.deserializeType(json, "isBeaconPayment", context, predicateType));
		}
		if(json.has("hasEffect")) {
			boolean hasEffect = helper.getBoolean(json, "hasEffect");
			settings.setHasEffectFunction(m->m.hasEffect() || hasEffect);
		}
		if(json.has("rarity")) {
			EnumRarity rarity = helper.deserializeType(json, "rarity", context, EnumRarity.class);
			settings.setDisplayRarityFunction(m->rarity);
		}
		if(json.has("burnTime")) {
			JsonObject functionJson = helper.getJsonObject(json, "burnTime");
			if(!functionJson.has("default")) {
				functionJson.addProperty("default", -1);
			}
			settings.setBurnTimeFunction(helper.deserializeType(json, "burnTime", context, intFunctionType));
		}
		return settings;
	}
}
