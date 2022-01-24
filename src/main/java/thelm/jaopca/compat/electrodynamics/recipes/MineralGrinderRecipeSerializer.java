package thelm.jaopca.compat.electrodynamics.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import thelm.jaopca.api.recipes.IRecipeSerializer;

public class MineralGrinderRecipeSerializer implements IRecipeSerializer {

	@Override
	public JsonElement get() {
		JsonObject json = new JsonObject();
		json.addProperty("type", "electrodynamics:mineral_crusher_recipe");
		return json;
	}
}
