package thelm.jaopca.ingredients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class WrappedIngredient extends AbstractIngredient {

	public static final ResourceLocation ID = new ResourceLocation("jaopca:wrapped");
	public static final IIngredientSerializer<WrappedIngredient> SERIALIZER = new Serializer();

	private final Ingredient wrapped;

	protected WrappedIngredient(Ingredient wrapped) {
		this.wrapped = wrapped;
	}

	public static WrappedIngredient of(Ingredient wrapped) {
		return new WrappedIngredient(wrapped);
	}

	public Ingredient getWrapped() {
		return wrapped;
	}

	@Override
	public boolean test(ItemStack stack) {
		return wrapped.test(stack);
	}

	@Override
	public ItemStack[] getItems() {
		return wrapped.getItems();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public boolean isSimple() {
		return wrapped.isSimple();
	}

	@Override
	public IntList getStackingIds() {
		return wrapped.getStackingIds();
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", ID.toString());
        json.add("wrapped", wrapped.toJson());
		return json;
	}

	@Override
	public IIngredientSerializer<WrappedIngredient> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements IIngredientSerializer<WrappedIngredient> {

		@Override
		public WrappedIngredient parse(JsonObject json) {
			return new WrappedIngredient(Ingredient.fromJson(json.get("wrapped")));
		}

		@Override
		public WrappedIngredient parse(FriendlyByteBuf buffer) {
			return new WrappedIngredient(Ingredient.fromNetwork(buffer));
		}

		@Override
		public void write(FriendlyByteBuf buffer, WrappedIngredient ingredient) {
			ingredient.wrapped.toNetwork(buffer);
		}
	}
}
