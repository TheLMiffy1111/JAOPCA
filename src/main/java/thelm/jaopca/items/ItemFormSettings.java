package thelm.jaopca.items;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.world.item.Rarity;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.items.IItemCreator;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.materials.IMaterial;

class ItemFormSettings implements IItemFormSettings {

	private IItemCreator itemCreator = JAOPCAItem::new;
	private ToIntFunction<IMaterial> maxStackSizeFunction = material->64;
	private Predicate<IMaterial> hasEffectFunction = material->material.hasEffect();
	private Function<IMaterial, Rarity> displayRarityFunction = material->material.getDisplayRarity();
	private ToIntFunction<IMaterial> burnTimeFunction = material->-1;

	@Override
	public IFormType getType() {
		return ItemFormType.INSTANCE;
	}

	@Override
	public IItemFormSettings setItemCreator(IItemCreator itemCreator) {
		this.itemCreator = itemCreator;
		return this;
	}

	@Override
	public IItemCreator getItemCreator() {
		return itemCreator;
	}

	@Override
	public IItemFormSettings setMaxStackSizeFunction(ToIntFunction<IMaterial> maxStackSizeFunction) {
		this.maxStackSizeFunction = maxStackSizeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getMaxStackSizeFunction() {
		return maxStackSizeFunction;
	}

	@Override
	public IItemFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction) {
		this.hasEffectFunction = hasEffectFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getHasEffectFunction() {
		return hasEffectFunction;
	}

	@Override
	public IItemFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction) {
		this.displayRarityFunction = displayRarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Rarity> getDisplayRarityFunction() {
		return displayRarityFunction;
	}

	@Override
	public IItemFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction) {
		this.burnTimeFunction = burnTimeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getBurnTimeFunction() {
		return burnTimeFunction;
	}
}
