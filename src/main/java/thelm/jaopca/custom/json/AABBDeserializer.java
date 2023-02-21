package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.AxisAlignedBB;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.utils.JsonHelper;

public class AABBDeserializer implements JsonDeserializer<AxisAlignedBB> {

	public static final AABBDeserializer INSTANCE = new AABBDeserializer();

	private AABBDeserializer() {}

	@Override
	public AxisAlignedBB deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		JsonObject json = helper.getJsonObject(jsonElement, "element");
		JsonArray jsonArrayFrom = helper.getJsonArray(json, "from");
		if(jsonArrayFrom.size() != 3) {
			throw new JsonParseException("Expected 3 from values, found: "+jsonArrayFrom.size());
		}
		JsonArray jsonArrayTo = helper.getJsonArray(json, "to");
		if(jsonArrayTo.size() != 3) {
			throw new JsonParseException("Expected 3 to values, found: "+jsonArrayTo.size());
		}
		double xFrom = helper.getDouble(jsonArrayFrom.get(0), "xFrom");
		double yFrom = helper.getDouble(jsonArrayFrom.get(1), "yFrom");
		double zFrom = helper.getDouble(jsonArrayFrom.get(2), "zFrom");
		double xTo = helper.getDouble(jsonArrayFrom.get(0), "xTo");
		double yTo = helper.getDouble(jsonArrayFrom.get(1), "yTo");
		double zTo = helper.getDouble(jsonArrayFrom.get(2), "zTo");
		return AxisAlignedBB.getBoundingBox(xFrom, yFrom, zFrom, xTo, yTo, zTo);
	}
}
