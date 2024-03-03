package thelm.jaopca.ingredients;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class EmptyIngredient extends AbstractIngredient {

	public static final ResourceLocation ID = new ResourceLocation("jaopca:empty");
	public static final IIngredientSerializer<EmptyIngredient> SERIALIZER = new Serializer();
	public static final EmptyIngredient INSTANCE = new EmptyIngredient();

	private static final ItemStack[] filteredMatchingStacks = new ItemStack[0];
	private static final IntList packedMatchingStacks = IntList.of();

	protected EmptyIngredient() {}

	@Override
	public boolean test(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack[] getItems() {
		return filteredMatchingStacks;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public IntList getStackingIds() {
		return packedMatchingStacks;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", ID.toString());
		return json;
	}

	@Override
	public IIngredientSerializer<EmptyIngredient> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements IIngredientSerializer<EmptyIngredient> {

		@Override
		public EmptyIngredient parse(JsonObject json) {
			return EmptyIngredient.INSTANCE;
		}

		@Override
		public EmptyIngredient parse(FriendlyByteBuf buffer) {
			return EmptyIngredient.INSTANCE;
		}

		@Override
		public void write(FriendlyByteBuf buffer, EmptyIngredient ingredient) {}
	}
}
