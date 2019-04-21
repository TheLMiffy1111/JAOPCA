package thelm.jaopca.api.helpers;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface IJsonHelper {

	boolean isString(JsonObject json, String memberName);

	boolean isString(JsonElement json);

	boolean isNumber(JsonObject json, String memberName);

	boolean isNumber(JsonElement json);

	boolean isBoolean(JsonObject json, String memberName);

	boolean isJsonArray(JsonObject json, String memberName);

	boolean isJsonPrimitive(JsonObject json, String memberName);

	boolean hasField(JsonObject json, String memberName);

	String getString(JsonElement json, String memberName);

	String getString(JsonObject json, String memberName);

	String getString(JsonObject json, String memberName, String fallback);

	boolean getBoolean(JsonElement json, String memberName);

	boolean getBoolean(JsonObject json, String memberName);

	boolean getBoolean(JsonObject json, String memberName, boolean fallback);

	double getDouble(JsonElement json, String memberName);

	double getDouble(JsonObject json, String memberName);

	double getDouble(JsonObject json, String memberName, double fallback);

	int getInt(JsonElement json, String memberName);

	int getInt(JsonObject json, String memberName);

	int getInt(JsonObject json, String memberName, int fallback);

	long getLong(JsonElement json, String memberName);

	long getLong(JsonObject json, String memberName);

	long getLong(JsonObject json, String memberName, int fallback);

	JsonObject getJsonObject(JsonElement json, String memberName);

	JsonObject getJsonObject(JsonObject json, String memberName);

	JsonObject getJsonObject(JsonObject json, String memberName, JsonObject fallback);

	JsonArray getJsonArray(JsonElement json, String memberName);

	JsonArray getJsonArray(JsonObject json, String memberName);

	JsonArray getJsonArray(JsonObject json, String memberName, JsonArray fallback);

	<T> T deserializeType(JsonElement json, String memberName, JsonDeserializationContext context, Type typeOfT);

	<T> T deserializeType(JsonObject json, String memberName, JsonDeserializationContext context, Type typeOfT);

	<T> T deserializeType(JsonObject json, String memberName, T fallback, JsonDeserializationContext context, Type typeOfT);

	String toSimpleString(JsonElement json);
}
