package thelm.jaopca.compat.flux;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.gson.JsonElement;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import szewek.fl.recipe.CountedIngredient;
import thelm.jaopca.compat.flux.recipes.AlloyingRecipeSupplier;
import thelm.jaopca.compat.flux.recipes.CompactingRecipeSupplier;
import thelm.jaopca.compat.flux.recipes.GrindingRecipeSupplier;
import thelm.jaopca.compat.flux.recipes.WashingRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class FluxHelper {

	public static final FluxHelper INSTANCE = new FluxHelper();

	private FluxHelper() {}

	static {

	}

	public Ingredient getCountedIngredient(Object obj, int count) {
		Stream<Ingredient.IItemList> itemListStream;
		if(obj instanceof Supplier<?>) {
			return getCountedIngredient(((Supplier<?>)obj).get(), count);
		}
		else if(obj instanceof CountedIngredient) {
			return (CountedIngredient)obj;
		}
		else if(obj instanceof String) {
			itemListStream = Stream.of(new Ingredient.TagList(MiscHelper.INSTANCE.getItemTag(new ResourceLocation((String)obj))));
		}
		else if(obj instanceof ResourceLocation) {
			itemListStream = Stream.of(new Ingredient.TagList(MiscHelper.INSTANCE.getItemTag((ResourceLocation)obj)));
		}
		else if(obj instanceof Tag<?>) {
			itemListStream = Stream.of(new Ingredient.TagList((Tag<Item>)obj));
		}
		else if(obj instanceof ItemStack) {
			itemListStream = Stream.of(new Ingredient.SingleItemList((ItemStack)obj));
		}
		else if(obj instanceof ItemStack[]) {
			itemListStream = Stream.of((ItemStack[])obj).map(Ingredient.SingleItemList::new);
		}
		else if(obj instanceof IItemProvider) {
			itemListStream = Stream.of(new Ingredient.SingleItemList(new ItemStack((IItemProvider)obj)));
		}
		else if(obj instanceof IItemProvider[]) {
			itemListStream = Stream.of((IItemProvider[])obj).map(ItemStack::new).map(Ingredient.SingleItemList::new);
		}
		else if(obj instanceof Ingredient.IItemList) {
			itemListStream = Stream.of((Ingredient.IItemList)obj);
		}
		else if(obj instanceof Ingredient.IItemList[]) {
			itemListStream = Stream.of((Ingredient.IItemList[])obj);
		}
		else if(obj instanceof JsonElement) {
			return Ingredient.fromJson((JsonElement)obj);
		}
		else {
			itemListStream = Stream.empty();
		}
		try {
			Constructor<CountedIngredient> ingConstructor = CountedIngredient.class.getDeclaredConstructor(Stream.class, int.class);
			ingConstructor.setAccessible(true);
			return ingConstructor.newInstance(itemListStream, count);
		}
		catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean registerGrindingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrindingRecipeSupplier(key, group, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerGrindingRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrindingRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerGrindingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrindingRecipeSupplier(key, group, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerGrindingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new GrindingRecipeSupplier(key, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerWashingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, group, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerWashingRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerWashingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, group, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerWashingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new WashingRecipeSupplier(key, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, group, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, group, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerCompactingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CompactingRecipeSupplier(key, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerAlloyingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyingRecipeSupplier(key, group, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerAlloyingRecipe(ResourceLocation key, Object input, int inputCount, Object secondInput, int secondInputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyingRecipeSupplier(key, input, inputCount, secondInput, secondInputCount, output, outputCount, experience, time));
	}

	public boolean registerAlloyingRecipe(ResourceLocation key, String group, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyingRecipeSupplier(key, group, input, inputCount, output, outputCount, experience, time));
	}

	public boolean registerAlloyingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, float experience, int time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new AlloyingRecipeSupplier(key, input, inputCount, output, outputCount, experience, time));
	}
}
