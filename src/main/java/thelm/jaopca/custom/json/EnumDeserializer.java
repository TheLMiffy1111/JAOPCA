package thelm.jaopca.custom.json;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.utils.JsonHelper;

public class EnumDeserializer implements JsonDeserializer<Enum<?>> {

	public static final EnumDeserializer INSTANCE = new EnumDeserializer();

	private EnumDeserializer() {}

	@Override
	public Enum<?> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		if(typeOfT instanceof Class && ((Class<?>)typeOfT).isEnum()) {
			Map<String, Enum<?>> stringToEnum = new TreeMap<>();
			for(Enum<?> value : ((Class<Enum<?>>)typeOfT).getEnumConstants()) {
				stringToEnum.put(value.name().toLowerCase(Locale.US), value);
			}
			String valueString = helper.getString(jsonElement, "value");
			Enum<?> value = stringToEnum.get(valueString);
			if(value == null) {
				throw new JsonSyntaxException("Invalid enum "+valueString);
			}
			return value;
		}
		throw new JsonParseException("Unable to deserialize "+helper.toSimpleString(jsonElement)+" into an enum function");
	}
}
