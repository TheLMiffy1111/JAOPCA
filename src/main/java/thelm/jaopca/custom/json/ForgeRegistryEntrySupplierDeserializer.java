package thelm.jaopca.custom.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryManager;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.utils.JsonHelper;

public class ForgeRegistryEntrySupplierDeserializer implements JsonDeserializer<Supplier<?>> {

	public static final ForgeRegistryEntrySupplierDeserializer INSTANCE = new ForgeRegistryEntrySupplierDeserializer();

	private ForgeRegistryEntrySupplierDeserializer() {}

	@Override
	public Supplier<?> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		Type[] typeArguments = ((ParameterizedType)typeOfT).getActualTypeArguments();
		Type parameterizedType = typeArguments[0];
		if(parameterizedType instanceof Class) {
			if(jsonElement.isJsonObject()) {
				JsonObject jsonObj = helper.getJsonObject(jsonElement, "value");
				ResourceLocation typeLocation = new ResourceLocation(helper.getString(jsonObj, "type"));
				ResourceLocation keyLocation = new ResourceLocation(helper.getString(jsonObj, "key"));
				return ()->RegistryManager.ACTIVE.getRegistry(typeLocation).getValue(keyLocation);
			}
		}
		throw new JsonParseException("Unable to deserialize "+helper.toSimpleString(jsonElement)+" into a forge registry entry");
	}
}
