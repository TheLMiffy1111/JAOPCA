package thelm.jaopca.api.fluids;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Rarity;
import net.minecraft.util.SoundEvent;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface IFluidFormSettings extends IFormSettings {

	IFluidFormSettings setFluidCreator(IFluidCreator fluidCreator);

	IFluidCreator getFluidCreator();

	IFluidFormSettings setMaxLevelFunction(ToIntFunction<IMaterial> maxLevelFunction);

	ToIntFunction<IMaterial> getMaxLevelFunction();

	IFluidFormSettings setTickRateFunction(ToIntFunction<IMaterial> tickRateFunction);

	ToIntFunction<IMaterial> getTickRateFunction();

	IFluidFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction);

	ToDoubleFunction<IMaterial> getExplosionResistanceFunction();

	IFluidFormSettings setCanSourcesMultiplyFunction(Predicate<IMaterial> canSourcesMultiplyFunction);

	Predicate<IMaterial> getCanSourcesMultiplyFunction();

	IFluidFormSettings setFluidAttributesCreator(IFluidAttributesCreator fluidAttributesCreator);

	IFluidAttributesCreator getFluidAttributesCreator();

	IFluidFormSettings setFillSoundSupplier(Supplier<SoundEvent> fillSoundSupplier);

	Supplier<SoundEvent> getFillSoundSupplier();

	IFluidFormSettings setEmptySoundSupplier(Supplier<SoundEvent> emptySoundSupplier);

	Supplier<SoundEvent> getEmptySoundSupplier();

	IFluidFormSettings setDensityFunction(ToIntFunction<IMaterial> densityFunction);

	ToIntFunction<IMaterial> getDensityFunction();

	IFluidFormSettings setViscosityFunction(ToIntFunction<IMaterial> viscosityFunction);

	ToIntFunction<IMaterial> getViscosityFunction();

	IFluidFormSettings setTemperatureFunction(ToIntFunction<IMaterial> temperatureFunction);

	ToIntFunction<IMaterial> getTemperatureFunction();

	IFluidFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction);

	Function<IMaterial, Rarity> getDisplayRarityFunction();

	IFluidFormSettings setFluidBlockCreator(IFluidBlockCreator fluidBlockCreator);

	IFluidBlockCreator getFluidBlockCreator();

	IFluidFormSettings setLevelDecreasePerBlockFunction(ToIntFunction<IMaterial> levelDecreasePerBlockFunction);

	ToIntFunction<IMaterial> getLevelDecreasePerBlockFunction();

	IFluidFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction);

	Function<IMaterial, Material> getMaterialFunction();

	IFluidFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction);

	Function<IMaterial, MaterialColor> getMaterialColorFunction();

	IFluidFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction);

	ToIntFunction<IMaterial> getLightValueFunction();

	IFluidFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction);

	ToDoubleFunction<IMaterial> getBlockHardnessFunction();

	IFluidFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction);

	ToIntFunction<IMaterial> getFlammabilityFunction();

	IFluidFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction);

	ToIntFunction<IMaterial> getFireSpreadSpeedFunction();

	IFluidFormSettings setIsFireSourceFunction(Predicate<IMaterial> isFireSourceFunction);

	Predicate<IMaterial> getIsFireSourceFunction();

	IFluidFormSettings setBucketItemCreator(IBucketItemCreator bucketItemCreator);

	IBucketItemCreator getBucketItemCreator();

	IFluidFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction);

	ToIntFunction<IMaterial> getItemStackLimitFunction();

	IFluidFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	IFluidFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();
}
