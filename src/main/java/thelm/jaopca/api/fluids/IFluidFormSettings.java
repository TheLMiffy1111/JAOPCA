package thelm.jaopca.api.fluids;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
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

	IFluidFormSettings setFluidTypeCreator(IFluidTypeCreator fluidTypeCreator);

	IFluidTypeCreator getFluidTypeCreator();

	IFluidFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction);

	ToIntFunction<IMaterial> getLightValueFunction();

	IFluidFormSettings setDensityFunction(ToIntFunction<IMaterial> densityFunction);

	ToIntFunction<IMaterial> getDensityFunction();

	IFluidFormSettings setTemperatureFunction(ToIntFunction<IMaterial> temperatureFunction);

	ToIntFunction<IMaterial> getTemperatureFunction();

	IFluidFormSettings setViscosityFunction(ToIntFunction<IMaterial> viscosityFunction);

	ToIntFunction<IMaterial> getViscosityFunction();

	IFluidFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction);

	Function<IMaterial, Rarity> getDisplayRarityFunction();

	IFluidFormSettings setFillSoundSupplier(Supplier<SoundEvent> fillSoundSupplier);

	Supplier<SoundEvent> getFillSoundSupplier();

	IFluidFormSettings setEmptySoundSupplier(Supplier<SoundEvent> emptySoundSupplier);

	Supplier<SoundEvent> getEmptySoundSupplier();

	IFluidFormSettings setVaporizeSoundSupplier(Supplier<SoundEvent> vaporizeSoundSupplier);

	Supplier<SoundEvent> getVaporizeSoundSupplier();

	IFluidFormSettings setMotionScaleFunction(ToDoubleFunction<IMaterial> motionScaleFunction);

	ToDoubleFunction<IMaterial> getMotionScaleFunction();

	IFluidFormSettings setCanPushEntityFunction(Predicate<IMaterial> canPushEntityFunction);

	Predicate<IMaterial> getCanPushEntityFunction();

	IFluidFormSettings setCanSwimFunction(Predicate<IMaterial> canSwimFunction);

	Predicate<IMaterial> getCanSwimFunction();

	IFluidFormSettings setFallDistanceModifierFunction(ToDoubleFunction<IMaterial> fallDistanceModifierFunction);

	ToDoubleFunction<IMaterial> getFallDistanceModifierFunction();

	IFluidFormSettings setCanExtinguishFunction(Predicate<IMaterial> canExtinguishFunction);

	Predicate<IMaterial> getCanExtinguishFunction();

	IFluidFormSettings setCanDrownFunction(Predicate<IMaterial> canDrownFunction);

	Predicate<IMaterial> getCanDrownFunction();

	IFluidFormSettings setSupportsBoatingFunction(Predicate<IMaterial> supportsBoatingFunction);

	Predicate<IMaterial> getSupportsBoatingFunction();

	IFluidFormSettings setCanHydrateFunction(Predicate<IMaterial> canHydrateFunction);

	Predicate<IMaterial> getCanHydrateFunction();

	IFluidFormSettings setCanConvertToSourceFunction(Predicate<IMaterial> canConvertToSourceFunction);

	Predicate<IMaterial> getCanConvertToSourceFunction();

	IFluidFormSettings setPathTypeFunction(Function<IMaterial, BlockPathTypes> pathTypeFunction);

	Function<IMaterial, BlockPathTypes> getPathTypeFunction();

	IFluidFormSettings setAdjacentPathTypeFunction(Function<IMaterial, BlockPathTypes> adjacentPathTypeFunction);

	Function<IMaterial, BlockPathTypes> getAdjacentPathTypeFunction();

	IFluidFormSettings setFluidBlockCreator(IFluidBlockCreator fluidBlockCreator);

	IFluidBlockCreator getFluidBlockCreator();

	IFluidFormSettings setLevelDecreasePerBlockFunction(ToIntFunction<IMaterial> levelDecreasePerBlockFunction);

	ToIntFunction<IMaterial> getLevelDecreasePerBlockFunction();

	IFluidFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction);

	Function<IMaterial, Material> getMaterialFunction();

	IFluidFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction);

	Function<IMaterial, MaterialColor> getMaterialColorFunction();

	IFluidFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction);

	ToDoubleFunction<IMaterial> getBlockHardnessFunction();

	IFluidFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction);

	ToIntFunction<IMaterial> getFlammabilityFunction();

	IFluidFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction);

	ToIntFunction<IMaterial> getFireSpreadSpeedFunction();

	IFluidFormSettings setIsFireSourceFunction(Predicate<IMaterial> isFireSourceFunction);

	Predicate<IMaterial> getIsFireSourceFunction();

	IFluidFormSettings setFireTimeFunction(ToIntFunction<IMaterial> fireTimeFunction);

	ToIntFunction<IMaterial> getFireTimeFunction();

	IFluidFormSettings setBucketItemCreator(IBucketItemCreator bucketItemCreator);

	IBucketItemCreator getBucketItemCreator();

	IFluidFormSettings setMaxStackSizeFunction(ToIntFunction<IMaterial> maxStackSizeFunction);

	ToIntFunction<IMaterial> getMaxStackSizeFunction();

	IFluidFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	IFluidFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();
}
