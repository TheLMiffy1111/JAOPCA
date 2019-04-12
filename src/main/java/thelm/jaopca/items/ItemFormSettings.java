package thelm.jaopca.items;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;

import net.minecraft.item.EnumRarity;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.items.IItemCreator;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public class ItemFormSettings implements IItemFormSettings {

	ItemFormSettings() {}
	
	private IItemCreator itemCreator = ItemJAOPCA::new;
	private ToIntFunction<IMaterial> itemStackLimitFunction = material->64;
	private Predicate<IMaterial> beaconPaymentFunction = material->false;
	private Predicate<IMaterial> hasEffectFunction = material->material.hasEffect();
	private Function<IMaterial, EnumRarity> rarityFunction = material->EnumRarity.COMMON;
	private ToIntFunction<IMaterial> burnTimeFunction = material->-1;

	@Override
	public IFormType<?> getType() {
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
	public IItemFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction) {
		this.itemStackLimitFunction = itemStackLimitFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getItemStackLimitFunction() {
		return itemStackLimitFunction;
	}

	@Override
	public IItemFormSettings setIsBeaconPaymentFunction(Predicate<IMaterial> beaconPaymentFunction) {
		this.beaconPaymentFunction = beaconPaymentFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getIsBeaconPaymentFunction() {
		return beaconPaymentFunction;
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
	public IItemFormSettings setRarityFunction(Function<IMaterial, EnumRarity> rarityFunction) {
		this.rarityFunction = rarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, EnumRarity> getRarityFunction() {
		return rarityFunction;
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
