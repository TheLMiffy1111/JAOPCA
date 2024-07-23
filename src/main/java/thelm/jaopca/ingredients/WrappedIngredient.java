package thelm.jaopca.ingredients;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class WrappedIngredient extends Ingredient {

	public static final Codec<WrappedIngredient> CODEC = RecordCodecBuilder.create(
			instance->instance.group(Ingredient.CODEC.fieldOf("wrapped").forGetter(WrappedIngredient::getWrapped)).
			apply(instance, WrappedIngredient::new));
	public static final Codec<WrappedIngredient> CODEC_NONEMPTY = RecordCodecBuilder.create(
			instance->instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("wrapped").forGetter(WrappedIngredient::getWrapped)).
			apply(instance, WrappedIngredient::new));

	private final Ingredient wrapped;

	protected WrappedIngredient(Ingredient wrapped) {
		super(Stream.empty(), IngredientTypes.WRAPPED_INGREDIENT_TYPE);
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
}
