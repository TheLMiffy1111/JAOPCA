package thelm.jaopca.compat.futurepack;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.gson.JsonElement;

import futurepack.api.ItemPredicateBase;
import futurepack.depend.api.ItemPredicate;
import futurepack.depend.api.ListPredicate;
import futurepack.depend.api.NullPredicate;
import futurepack.depend.api.VanillaTagPredicate;
import futurepack.depend.api.helper.HelperJSON;
import mekanism.api.providers.IItemProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.jaopca.compat.futurepack.recipes.CentrifugeRecipeSerializer;

public class FuturepackHelper {

	public static final FuturepackHelper INSTANCE = new FuturepackHelper();

	private FuturepackHelper() {}

	public boolean registerCentrifugeRecipe(ResourceLocation key, Object input, int inputCount, int support, int time, Object... output) {
		return FuturepackDataInjector.registerRecipe("centrifuge", key, new CentrifugeRecipeSerializer(key, input, inputCount, support, time, output));
	}
}
