package thelm.jaopca.api.items;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import net.minecraft.item.EnumRarity;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface IItemFormSettings extends IFormSettings {

	IItemFormSettings setItemCreator(IItemCreator itemCreator);

	IItemCreator getItemCreator();

	IItemFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction);

	ToIntFunction<IMaterial> getItemStackLimitFunction();

	IItemFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	IItemFormSettings setDisplayRarityFunction(Function<IMaterial, EnumRarity> displayRarityFunction);

	Function<IMaterial, EnumRarity> getDisplayRarityFunction();

	IItemFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();

	IItemFormSettings setItemModelFunctionCreator(IItemModelFunctionCreator itemModelFunctionCreator);

	IItemModelFunctionCreator getItemModelFunctionCreator();
}
