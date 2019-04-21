package thelm.jaopca.custom.json;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import thelm.jaopca.api.helpers.IJsonHelper;
import thelm.jaopca.utils.JsonHelper;

public class VoxelShapeDeserializer implements JsonDeserializer<VoxelShape> {

	public static final VoxelShapeDeserializer INSTANCE = new VoxelShapeDeserializer();

	private VoxelShapeDeserializer() {}

	@Override
	public VoxelShape deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		IJsonHelper helper = JsonHelper.INSTANCE;
		VoxelShape shape = VoxelShapes.empty();
		JsonArray jsonArray;
		if(jsonElement.isJsonArray()) {
			jsonArray = helper.getJsonArray(jsonElement, "array");
		}
		else {
			jsonArray = new JsonArray();
			jsonArray.add(jsonElement);
		}
		for(JsonElement element : jsonArray) {
			JsonObject json = helper.getJsonObject(element, "element");
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
			shape = VoxelShapes.or(shape, VoxelShapes.create(xFrom, yFrom, zFrom, xTo, yTo, zTo));
		}
		return shape;
	}
}
