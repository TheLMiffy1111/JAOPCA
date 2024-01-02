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
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.futurepack.recipes.ZentrifugeRecipeSupplier;

public class FuturepackHelper {

	public static final FuturepackHelper INSTANCE = new FuturepackHelper();

	private FuturepackHelper() {}

	public ItemPredicateBase getItemPredicateBase(Object obj, int count) {
		if(obj instanceof Supplier<?>) {
			return getItemPredicateBase(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof ItemPredicateBase) {
			return (ItemPredicateBase)obj;
		}
		else if(obj instanceof String) {
			return new VanillaTagPredicate((String)obj, count);
		}
		else if(obj instanceof ResourceLocation) {
			return new VanillaTagPredicate((ResourceLocation)obj, count);
		}
		else if(obj instanceof ItemStack) {
			return new ItemPredicate((ItemStack)obj);
		}
		else if(obj instanceof ItemStack[]) {
			return new ListPredicate(true, Stream.of((ItemStack[])obj).map(ItemPredicate::new).toArray(ItemPredicateBase[]::new));
		}
		else if(obj instanceof IItemProvider) {
			return new ItemPredicate(((IItemProvider)obj).asItem(), count);
		}
		else if(obj instanceof IItemProvider[]) {
			return new ListPredicate(true, Stream.of((IItemProvider[])obj).map(ItemStack::new).map(ItemPredicate::new).toArray(ItemPredicateBase[]::new));
		}
		else if(obj instanceof JsonElement) {
			return HelperJSON.getItemPredicateFromJSON((JsonElement)obj);
		}
		return new NullPredicate();
	}
	
	public boolean registerZentrifugeRecipe(ResourceLocation key, Object input, int inputCount, int support, int time, Object... output) {
		return FuturepackDataInjector.registerZentrifugeRecipe(key, new ZentrifugeRecipeSupplier(key, input, inputCount, support, time, output));
	}
}
