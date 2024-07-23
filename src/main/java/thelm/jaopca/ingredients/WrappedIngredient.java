package thelm.jaopca.ingredients;

import java.util.Arrays;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public class WrappedIngredient implements ICustomIngredient {

	public static final MapCodec<WrappedIngredient> CODEC = Ingredient.CODEC.fieldOf("wrapped").
			xmap(WrappedIngredient::new, WrappedIngredient::getWrapped);

	private final Ingredient wrapped;

	protected WrappedIngredient(Ingredient wrapped) {
		this.wrapped = wrapped;
	}

	public static Ingredient of(Ingredient wrapped) {
		return new WrappedIngredient(wrapped).toVanilla();
	}


	public Ingredient getWrapped() {
		return wrapped;
	}

	@Override
	public IngredientType<?> getType() {
		return IngredientTypes.WRAPPED_INGREDIENT_TYPE.get();
	}

	@Override
	public boolean test(ItemStack stack) {
		return wrapped.test(stack);
	}

	@Override
	public Stream<ItemStack> getItems() {
		return Arrays.stream(wrapped.getItems());
	}

	@Override
	public boolean isSimple() {
		return wrapped.isSimple();
	}
}
