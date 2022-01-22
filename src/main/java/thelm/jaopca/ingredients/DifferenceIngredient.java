package thelm.jaopca.ingredients;

import java.util.Arrays;
import java.util.stream.Stream;

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

// Modified from Mantle 1.18
public class DifferenceIngredient extends Ingredient {

	public static final ResourceLocation ID = new ResourceLocation("jaopca:difference");
	public static final IIngredientSerializer<DifferenceIngredient> SERIALIZER = new Serializer();

	private final Ingredient base;
	private final Ingredient subtracted;
	private ItemStack[] filteredMatchingStacks;
	private IntList packedMatchingStacks;

	protected DifferenceIngredient(Ingredient base, Ingredient subtracted) {
		super(Stream.empty());
		this.base = base;
		this.subtracted = subtracted;
	}

	public static DifferenceIngredient of(Ingredient base, Ingredient subtracted) {
		return new DifferenceIngredient(base, subtracted);
	}

	@Override
	public boolean test(ItemStack stack) {
		if(stack == null || stack.isEmpty()) {
			return false;
		}
		return base.test(stack) && !subtracted.test(stack);
	}

	@Override
	public ItemStack[] getItems() {
		if(filteredMatchingStacks == null) {
			filteredMatchingStacks = Arrays.stream(base.getItems()).
					filter(stack->!subtracted.test(stack)).
					toArray(ItemStack[]::new);
		}
		return filteredMatchingStacks;
	}

	@Override
	public boolean isEmpty() {
		return getItems().length == 0;
	}

	@Override
	public boolean isSimple() {
		return base.isSimple() && subtracted.isSimple();
	}

	@Override
	protected void invalidate() {
		super.invalidate();
		this.filteredMatchingStacks = null;
		this.packedMatchingStacks = null;
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
		json.add("base", base.toJson());
		json.add("subtracted", subtracted.toJson());
		return json;
	}

	@Override
	public IIngredientSerializer<DifferenceIngredient> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer implements IIngredientSerializer<DifferenceIngredient> {

		@Override
		public DifferenceIngredient parse(JsonObject json) {
			Ingredient base = Ingredient.fromJson(json.get("base"));
			Ingredient without = Ingredient.fromJson(json.get("subtracted"));
			return new DifferenceIngredient(base, without);
		}

		@Override
		public DifferenceIngredient parse(FriendlyByteBuf buffer) {
			Ingredient base = Ingredient.fromNetwork(buffer);
			Ingredient without = Ingredient.fromNetwork(buffer);
			return new DifferenceIngredient(base, without);
		}

		@Override
		public void write(FriendlyByteBuf buffer, DifferenceIngredient ingredient) {
			CraftingHelper.write(buffer, ingredient.base);
			CraftingHelper.write(buffer, ingredient.subtracted);
		}
	}
}
