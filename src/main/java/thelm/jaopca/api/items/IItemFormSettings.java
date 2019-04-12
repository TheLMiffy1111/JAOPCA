package thelm.jaopca.api.items;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.google.common.base.Function;

import net.minecraft.item.EnumRarity;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface IItemFormSettings extends IFormSettings {

	IItemFormSettings setItemCreator(IItemCreator itemCreator);

	IItemCreator getItemCreator();

	IItemFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction);

	ToIntFunction<IMaterial> getItemStackLimitFunction();

	IItemFormSettings setIsBeaconPaymentFunction(Predicate<IMaterial> beaconPaymentFunction);

	Predicate<IMaterial> getIsBeaconPaymentFunction();

	IItemFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	IItemFormSettings setRarityFunction(Function<IMaterial, EnumRarity> rarityFunction);

	Function<IMaterial, EnumRarity> getRarityFunction();

	IItemFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();
}
