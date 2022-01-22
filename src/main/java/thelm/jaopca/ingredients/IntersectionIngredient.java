package thelm.jaopca.ingredients;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import thelm.jaopca.utils.JsonHelper;

// Modified from Mantle 1.18
public class IntersectionIngredient extends Ingredient {

	public static final ResourceLocation ID = new ResourceLocation("jaopca:intersection");
	public static final IIngredientSerializer<IntersectionIngredient> SERIALIZER = new Serializer();

	private final List<Ingredient> ingredients;
	private ItemStack[] intersectedMatchingStacks;
	private IntList packedMatchingStacks;

	protected IntersectionIngredient(List<Ingredient> ingredients) {
		super(Stream.empty());
		this.ingredients = ingredients;
	}

	public static IntersectionIngredient of(List<Ingredient> ingredients) {
		return new IntersectionIngredient(ingredients);
	}

	public static IntersectionIngredient of(Ingredient... ingredients) {
		return of(ImmutableList.copyOf(ingredients));
	}

	@Override
	public boolean test(ItemStack stack) {
		if(stack == null || stack.isEmpty() || ingredients.isEmpty()) {
			return false;
		}
		return ingredients.stream().allMatch(ing->ing.test(stack));
	}

	@Override
	public ItemStack[] getItems() {
		if(intersectedMatchingStacks == null) {
			if(ingredients.isEmpty()) {
				intersectedMatchingStacks = new ItemStack[0];
			}
			else {
				intersectedMatchingStacks = Arrays.stream(ingredients.get(0).getItems()).
						filter(stack->ingredients.stream().allMatch(ing->ing.test(stack))).
						toArray(ItemStack[]::new);
			}
		}
		return intersectedMatchingStacks;
	}

	@Override
	public boolean isEmpty() {
		return getItems().length == 0;
	}

	@Override
	public boolean isSimple() {
		for(Ingredient ingredient : ingredients) {
			if(!ingredient.isSimple()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void invalidate() {
		super.invalidate();
		intersectedMatchingStacks = null;
		packedMatchingStacks = null;
	}

	@Override
	public IntList getStackingIds() {
		if(packedMatchingStacks == null) {
			ItemStack[] matchingStacks = getItems();
			packedMatchingStacks = new IntArrayList(matchingStacks.length);
			for(ItemStack stack : matchingStacks) {
				packedMatchingStacks.add(StackedContents.getStackingIndex(stack));
			}
			packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
		}
		return packedMatchingStacks;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", ID.toString());
		JsonArray array = new JsonArray();
		for(Ingredient ingredient : ingredients) {
			array.add(ingredient.toJson());
		}
		json.add("ingredients", array);
		return json;
	}

	@Override
	public IIngredientSerializer<IntersectionIngredient> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements IIngredientSerializer<IntersectionIngredient> {

		@Override
		public IntersectionIngredient parse(JsonObject json) {
			ImmutableList.Builder<Ingredient> builder = ImmutableList.builder();
			for(JsonElement element : JsonHelper.INSTANCE.getJsonArray(json, "ingredients")) {
				builder.add(Ingredient.fromJson(element));
			}
			return new IntersectionIngredient(builder.build());
		}

		@Override
		public IntersectionIngredient parse(FriendlyByteBuf buffer) {
			int size = buffer.readVarInt();
			ImmutableList.Builder<Ingredient> builder = ImmutableList.builder();
			for(int i = 0; i < size; i++) {
				builder.add(Ingredient.fromNetwork(buffer));
			}
			return new IntersectionIngredient(builder.build());
		}

		@Override
		public void write(FriendlyByteBuf buffer, IntersectionIngredient intersection) {
			buffer.writeVarInt(intersection.ingredients.size());
			for(Ingredient ingredient : intersection.ingredients) {
				CraftingHelper.write(buffer, ingredient);
			}
		}
	}
}
