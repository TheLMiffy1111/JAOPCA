package thelm.jaopca.utils;

import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import thelm.jaopca.api.helpers.IJsonHelper;

public class JsonHelper implements IJsonHelper {

	public static final JsonHelper INSTANCE = new JsonHelper();

	private JsonHelper() {}

	@Override
	public boolean isString(JsonObject json, String memberName) {
		return !isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isString();
	}

	@Override
	public boolean isString(JsonElement json) {
		return !json.isJsonPrimitive() ? false : json.getAsJsonPrimitive().isString();
	}

	@Override
	public boolean isNumber(JsonObject json, String memberName) {
		return !isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isNumber();
	}

	@Override
	public boolean isNumber(JsonElement json) {
		return !json.isJsonPrimitive() ? false : json.getAsJsonPrimitive().isNumber();
	}

	@Override
	public boolean isBoolean(JsonObject json, String memberName) {
		return !isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isBoolean();
	}

	@Override
	public boolean isJsonArray(JsonObject json, String memberName) {
		return !hasField(json, memberName) ? false : json.get(memberName).isJsonArray();
	}

	@Override
	public boolean isJsonPrimitive(JsonObject json, String memberName) {
		return !hasField(json, memberName) ? false : json.get(memberName).isJsonPrimitive();
	}

	@Override
	public boolean hasField(JsonObject json, String memberName) {
		return json == null ? false : json.has(memberName);
	}

	@Override
	public String getString(JsonElement json, String memberName) {
		if(json.isJsonPrimitive()) {
			return json.getAsString();
		}
		else {
			throw new JsonSyntaxException("Expected "+memberName+" to be a string, was "+toSimpleString(json));
		}
	}

	@Override
	public String getString(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			return getString(json.get(memberName), memberName);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName+", expected to find a string");
		}
	}

	@Override
	public String getString(JsonObject json, String memberName, String fallback) {
		return json.has(memberName) ? getString(json.get(memberName), memberName) : fallback;
	}

	@Override
	public boolean getBoolean(JsonElement json, String memberName) {
		if(json.isJsonPrimitive()) {
			return json.getAsBoolean();
		}
		else {
			throw new JsonSyntaxException("Expected "+memberName+" to be a boolean, was "+toSimpleString(json));
		}
	}

	@Override
	public boolean getBoolean(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			return getBoolean(json.get(memberName), memberName);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName+", expected to find a boolean");
		}
	}

	@Override
	public boolean getBoolean(JsonObject json, String memberName, boolean fallback) {
		return json.has(memberName) ? getBoolean(json.get(memberName), memberName) : fallback;
	}

	@Override
	public double getDouble(JsonElement json, String memberName) {
		if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
			return json.getAsDouble();
		}
		else {
			throw new JsonSyntaxException("Expected "+memberName+" to be a number, was "+toSimpleString(json));
		}
	}

	@Override
	public double getDouble(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			return getDouble(json.get(memberName), memberName);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName+", expected to find a number");
		}
	}

	@Override
	public double getDouble(JsonObject json, String memberName, double fallback) {
		return json.has(memberName) ? getDouble(json.get(memberName), memberName) : fallback;
	}

	@Override
	public int getInt(JsonElement json, String memberName) {
		if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
			return json.getAsInt();
		}
		else {
			throw new JsonSyntaxException("Expected "+memberName+" to be an number, was "+toSimpleString(json));
		}
	}

	@Override
	public int getInt(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			return getInt(json.get(memberName), memberName);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName+", expected to find a number");
		}
	}

	@Override
	public int getInt(JsonObject json, String memberName, int fallback) {
		return json.has(memberName) ? getInt(json.get(memberName), memberName) : fallback;
	}

	@Override
	public long getLong(JsonElement json, String memberName) {
		if(json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
			return json.getAsLong();
		}
		else {
			throw new JsonSyntaxException("Expected "+memberName+" to be an number, was "+toSimpleString(json));
		}
	}

	@Override
	public long getLong(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			return getLong(json.get(memberName), memberName);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName+", expected to find a number");
		}
	}

	@Override
	public long getLong(JsonObject json, String memberName, int fallback) {
		return json.has(memberName) ? getLong(json.get(memberName), memberName) : fallback;
	}

	@Override
	public JsonObject getJsonObject(JsonElement json, String memberName) {
		if(json.isJsonObject()) {
			return json.getAsJsonObject();
		}
		else {
			throw new JsonSyntaxException("Expected "+memberName+" to be an object, was "+toSimpleString(json));
		}
	}

	@Override
	public JsonObject getJsonObject(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			return getJsonObject(json.get(memberName), memberName);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName+", expected to find an object");
		}
	}

	@Override
	public JsonObject getJsonObject(JsonObject json, String memberName, JsonObject fallback) {
		return json.has(memberName) ? getJsonObject(json.get(memberName), memberName) : fallback;
	}

	@Override
	public JsonArray getJsonArray(JsonElement json, String memberName) {
		if(json.isJsonArray()) {
			return json.getAsJsonArray();
		}
		else {
			throw new JsonSyntaxException("Expected "+memberName+" to be an array, was "+toSimpleString(json));
		}
	}

	@Override
	public JsonArray getJsonArray(JsonObject json, String memberName) {
		if(json.has(memberName)) {
			return getJsonArray(json.get(memberName), memberName);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName+", expected to find an array");
		}
	}

	@Override
	public JsonArray getJsonArray(JsonObject json, String memberName, JsonArray fallback) {
		return json.has(memberName) ? getJsonArray(json.get(memberName), memberName) : fallback;
	}

	@Override
	public <T> T deserializeType(JsonElement json, String memberName, JsonDeserializationContext context, Type typeOfT) {
		if(json != null) {
			return context.deserialize(json, typeOfT);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName);
		}
	}

	@Override
	public <T> T deserializeType(JsonObject json, String memberName, JsonDeserializationContext context, Type typeOfT) {
		if(json.has(memberName)) {
			return deserializeType(json.get(memberName), memberName, context, typeOfT);
		}
		else {
			throw new JsonSyntaxException("Missing "+memberName);
		}
	}

	@Override
	public <T> T deserializeType(JsonObject json, String memberName, T fallback, JsonDeserializationContext context, Type typeOfT) {
		return (T)(json.has(memberName) ? deserializeType(json.get(memberName), memberName, context, typeOfT) : fallback);
	}

	@Override
	public String toSimpleString(JsonElement json) {
		String s = StringUtils.abbreviateMiddle(String.valueOf(json), "...", 10);
		if(json == null) {
			return "null (missing)";
		}
		else if(json.isJsonNull()) {
			return "null (json)";
		}
		else if(json.isJsonArray()) {
			return "an array ("+s+")";
		}
		else if(json.isJsonObject()) {
			return "an object ("+s+")";
		}
		else {
			if(json.isJsonPrimitive()) {
				JsonPrimitive jsonprimitive = json.getAsJsonPrimitive();
				if(jsonprimitive.isNumber()) {
					return "a number ("+s+")";
				}
				if(jsonprimitive.isBoolean()) {
					return "a boolean ("+s+")";
				}
				if(jsonprimitive.isString()) {
					return "a string ("+s+")";
				}
			}
			return s;
		}
	}
}
