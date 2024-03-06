package thelm.jaopca.fluids;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import thelm.jaopca.api.custom.MapColorType;
import thelm.jaopca.api.fluids.IBucketItemCreator;
import thelm.jaopca.api.fluids.IFluidBlockCreator;
import thelm.jaopca.api.fluids.IFluidCreator;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IFluidTypeCreator;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.functions.MaterialDoubleFunction;
import thelm.jaopca.api.functions.MaterialIntFunction;
import thelm.jaopca.api.functions.MaterialMappedFunction;
import thelm.jaopca.api.functions.MaterialPredicate;
import thelm.jaopca.api.materials.IMaterial;

class FluidFormSettings implements IFluidFormSettings {

	private IFluidCreator fluidCreator = JAOPCAFluid::new;
	private ToIntFunction<IMaterial> maxLevelFunction = MaterialIntFunction.of(8);
	private ToIntFunction<IMaterial> tickRateFunction = MaterialIntFunction.of(5);
	private ToDoubleFunction<IMaterial> explosionResistanceFunction = MaterialDoubleFunction.of(100);
	private IFluidTypeCreator fluidTypeCreator = JAOPCAFluidType::new;
	private ToIntFunction<IMaterial> lightValueFunction = MaterialIntFunction.of(0);
	private ToIntFunction<IMaterial> densityFunction = MaterialIntFunction.of(1000);
	private ToIntFunction<IMaterial> temperatureFunction = MaterialIntFunction.of(300);
	private ToIntFunction<IMaterial> viscosityFunction = MaterialIntFunction.of(1000);
	private Function<IMaterial, Rarity> displayRarityFunction = MaterialMappedFunction.of(Rarity.class, Rarity.COMMON);
	private Supplier<SoundEvent> fillSoundSupplier = ()->SoundEvents.BUCKET_FILL;
	private Supplier<SoundEvent> emptySoundSupplier = ()->SoundEvents.BUCKET_EMPTY;
	private Supplier<SoundEvent> vaporizeSoundSupplier = ()->SoundEvents.FIRE_EXTINGUISH;
	private ToDoubleFunction<IMaterial> motionScaleFunction = MaterialDoubleFunction.of(0.014);
	private Predicate<IMaterial> canPushEntityFunction = MaterialPredicate.of(true);
	private Predicate<IMaterial> canSwimFunction = MaterialPredicate.of(true);
	private ToDoubleFunction<IMaterial> fallDistanceModifierFunction = MaterialDoubleFunction.of(0.5);
	private Predicate<IMaterial> canExtinguishFunction = MaterialPredicate.of(false);
	private Predicate<IMaterial> canDrownFunction = MaterialPredicate.of(true);
	private Predicate<IMaterial> supportsBoatingFunction = MaterialPredicate.of(false);
	private Predicate<IMaterial> canHydrateFunction = MaterialPredicate.of(false);
	private Predicate<IMaterial> canConvertToSourceFunction = MaterialPredicate.of(false);
	private Function<IMaterial, BlockPathTypes> pathTypeFunction = MaterialMappedFunction.of(BlockPathTypes.class, BlockPathTypes.WATER);
	private Function<IMaterial, BlockPathTypes> adjacentPathTypeFunction = MaterialMappedFunction.of(BlockPathTypes.class, BlockPathTypes.WATER_BORDER);
	private IFluidBlockCreator fluidBlockCreator = JAOPCAFluidBlock::new;
	private ToIntFunction<IMaterial> levelDecreasePerBlockFunction = MaterialIntFunction.of(1);
	private Function<IMaterial, MapColor> mapColorFunction = MapColorType.functionOf(MapColor.WATER);
	//material->{
	//	int color = material.getColor();
	//	return Arrays.stream(MaterialColor.COLORS).filter(Objects::nonNull).
	//			min((matColor1, matColor2)->Integer.compare(
	//					MiscHelper.INSTANCE.squareColorDifference(color, matColor1.colorValue),
	//					MiscHelper.INSTANCE.squareColorDifference(color, matColor2.colorValue))).
	//			orElse(MaterialColor.WATER);
	//};
	private ToDoubleFunction<IMaterial> blockHardnessFunction = MaterialDoubleFunction.of(100);
	private ToIntFunction<IMaterial> flammabilityFunction = MaterialIntFunction.of(0);
	private ToIntFunction<IMaterial> fireSpreadSpeedFunction = MaterialIntFunction.of(0);
	private Predicate<IMaterial> isFireSourceFunction = MaterialPredicate.of(false);
	private ToIntFunction<IMaterial> fireTimeFunction = MaterialIntFunction.of(1);
	private IBucketItemCreator bucketItemCreator = JAOPCABucketItem::new;
	private ToIntFunction<IMaterial> maxStackSizeFunction = MaterialIntFunction.of(1);
	private Predicate<IMaterial> hasEffectFunction = MaterialPredicate.of(false);
	private ToIntFunction<IMaterial> burnTimeFunction = MaterialIntFunction.of(-1);

	@Override
	public IFormType getType() {
		return FluidFormType.INSTANCE;
	}

	@Override
	public IFluidFormSettings setFluidCreator(IFluidCreator fluidCreator) {
		this.fluidCreator = fluidCreator;
		return this;
	}

	@Override
	public IFluidCreator getFluidCreator() {
		return fluidCreator;
	}

	@Override
	public IFluidFormSettings setMaxLevelFunction(ToIntFunction<IMaterial> maxLevelFunction) {
		this.maxLevelFunction = maxLevelFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getMaxLevelFunction() {
		return maxLevelFunction;
	}

	@Override
	public IFluidFormSettings setTickRateFunction(ToIntFunction<IMaterial> tickRateFunction) {
		this.tickRateFunction = tickRateFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getTickRateFunction() {
		return tickRateFunction;
	}

	@Override
	public IFluidFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction) {
		this.explosionResistanceFunction = explosionResistanceFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getExplosionResistanceFunction() {
		return explosionResistanceFunction;
	}

	@Override
	public IFluidFormSettings setFluidTypeCreator(IFluidTypeCreator fluidTypeCreator) {
		this.fluidTypeCreator = fluidTypeCreator;
		return this;
	}

	@Override
	public IFluidTypeCreator getFluidTypeCreator() {
		return fluidTypeCreator;
	}

	@Override
	public IFluidFormSettings setDensityFunction(ToIntFunction<IMaterial> densityFunction) {
		this.densityFunction = densityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getDensityFunction() {
		return densityFunction;
	}

	@Override
	public IFluidFormSettings setViscosityFunction(ToIntFunction<IMaterial> viscosityFunction) {
		this.viscosityFunction = viscosityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getViscosityFunction() {
		return viscosityFunction;
	}

	@Override
	public IFluidFormSettings setTemperatureFunction(ToIntFunction<IMaterial> temperatureFunction) {
		this.temperatureFunction = temperatureFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getTemperatureFunction() {
		return temperatureFunction;
	}

	@Override
	public IFluidFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction) {
		this.displayRarityFunction = displayRarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Rarity> getDisplayRarityFunction() {
		return displayRarityFunction;
	}

	@Override
	public IFluidFormSettings setFillSoundSupplier(Supplier<SoundEvent> fillSoundSupplier) {
		this.fillSoundSupplier = fillSoundSupplier;
		return this;
	}

	@Override
	public Supplier<SoundEvent> getFillSoundSupplier() {
		return fillSoundSupplier;
	}

	@Override
	public IFluidFormSettings setEmptySoundSupplier(Supplier<SoundEvent> emptySoundSupplier) {
		this.emptySoundSupplier = emptySoundSupplier;
		return this;
	}

	@Override
	public Supplier<SoundEvent> getEmptySoundSupplier() {
		return emptySoundSupplier;
	}

	@Override
	public IFluidFormSettings setVaporizeSoundSupplier(Supplier<SoundEvent> vaporizeSoundSupplier) {
		this.vaporizeSoundSupplier = vaporizeSoundSupplier;
		return this;
	}

	@Override
	public Supplier<SoundEvent> getVaporizeSoundSupplier() {
		return vaporizeSoundSupplier;
	}

	@Override
	public IFluidFormSettings setMotionScaleFunction(ToDoubleFunction<IMaterial> motionScaleFunction) {
		this.motionScaleFunction = motionScaleFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getMotionScaleFunction() {
		return motionScaleFunction;
	}

	@Override
	public IFluidFormSettings setCanPushEntityFunction(Predicate<IMaterial> canPushEntityFunction) {
		this.canPushEntityFunction = canPushEntityFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getCanPushEntityFunction() {
		return canPushEntityFunction;
	}

	@Override
	public IFluidFormSettings setCanSwimFunction(Predicate<IMaterial> canSwimFunction) {
		this.canSwimFunction = canSwimFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getCanSwimFunction() {
		return canSwimFunction;
	}

	@Override
	public IFluidFormSettings setFallDistanceModifierFunction(ToDoubleFunction<IMaterial> fallDistanceModifierFunction) {
		this.fallDistanceModifierFunction = fallDistanceModifierFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getFallDistanceModifierFunction() {
		return fallDistanceModifierFunction;
	}

	@Override
	public IFluidFormSettings setCanExtinguishFunction(Predicate<IMaterial> canExtinguishFunction) {
		this.canExtinguishFunction = canExtinguishFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getCanExtinguishFunction() {
		return canExtinguishFunction;
	}

	@Override
	public IFluidFormSettings setCanDrownFunction(Predicate<IMaterial> canDrownFunction) {
		this.canDrownFunction = canDrownFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getCanDrownFunction() {
		return canDrownFunction;
	}

	@Override
	public IFluidFormSettings setSupportsBoatingFunction(Predicate<IMaterial> supportsBoatingFunction) {
		this.supportsBoatingFunction = supportsBoatingFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getSupportsBoatingFunction() {
		return supportsBoatingFunction;
	}

	@Override
	public IFluidFormSettings setCanHydrateFunction(Predicate<IMaterial> canHydrateFunction) {
		this.canHydrateFunction = canHydrateFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getCanHydrateFunction() {
		return canHydrateFunction;
	}

	@Override
	public IFluidFormSettings setCanConvertToSourceFunction(Predicate<IMaterial> canConvertToSourceFunction) {
		this.canConvertToSourceFunction = canConvertToSourceFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getCanConvertToSourceFunction() {
		return canConvertToSourceFunction;
	}

	@Override
	public IFluidFormSettings setPathTypeFunction(Function<IMaterial, BlockPathTypes> pathTypeFunction) {
		this.pathTypeFunction = pathTypeFunction;
		return this;
	}

	@Override
	public Function<IMaterial, BlockPathTypes> getPathTypeFunction() {
		return pathTypeFunction;
	}

	@Override
	public IFluidFormSettings setAdjacentPathTypeFunction(Function<IMaterial, BlockPathTypes> adjacentPathTypeFunction) {
		this.adjacentPathTypeFunction = adjacentPathTypeFunction;
		return this;
	}

	@Override
	public Function<IMaterial, BlockPathTypes> getAdjacentPathTypeFunction() {
		return adjacentPathTypeFunction;
	}

	@Override
	public IFluidFormSettings setFluidBlockCreator(IFluidBlockCreator fluidBlockCreator) {
		this.fluidBlockCreator = fluidBlockCreator;
		return this;
	}

	@Override
	public IFluidBlockCreator getFluidBlockCreator() {
		return fluidBlockCreator;
	}

	@Override
	public IFluidFormSettings setLevelDecreasePerBlockFunction(ToIntFunction<IMaterial> levelDecreasePerBlockFunction) {
		this.levelDecreasePerBlockFunction = levelDecreasePerBlockFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getLevelDecreasePerBlockFunction() {
		return levelDecreasePerBlockFunction;
	}

	@Override
	public IFluidFormSettings setMapColorFunction(Function<IMaterial, MapColor> mapColorFunction) {
		this.mapColorFunction = mapColorFunction;
		return this;
	}

	@Override
	public Function<IMaterial, MapColor> getMapColorFunction() {
		return mapColorFunction;
	}

	@Override
	public IFluidFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction) {
		this.lightValueFunction = lightValueFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getLightValueFunction() {
		return lightValueFunction;
	}

	@Override
	public IFluidFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction) {
		this.blockHardnessFunction = blockHardnessFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getBlockHardnessFunction() {
		return blockHardnessFunction;
	}

	@Override
	public IFluidFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction) {
		this.flammabilityFunction = flammabilityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFlammabilityFunction() {
		return flammabilityFunction;
	}

	@Override
	public IFluidFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction) {
		this.fireSpreadSpeedFunction = fireSpreadSpeedFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFireSpreadSpeedFunction() {
		return fireSpreadSpeedFunction;
	}

	@Override
	public IFluidFormSettings setIsFireSourceFunction(Predicate<IMaterial> isFireSourceFunction) {
		this.isFireSourceFunction = isFireSourceFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getIsFireSourceFunction() {
		return isFireSourceFunction;
	}

	@Override
	public IFluidFormSettings setFireTimeFunction(ToIntFunction<IMaterial> fireTimeFunction) {
		this.fireTimeFunction = fireTimeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFireTimeFunction() {
		return fireTimeFunction;
	}

	@Override
	public IFluidFormSettings setBucketItemCreator(IBucketItemCreator bucketItemCreator) {
		this.bucketItemCreator = bucketItemCreator;
		return this;
	}

	@Override
	public IBucketItemCreator getBucketItemCreator() {
		return bucketItemCreator;
	}

	@Override
	public IFluidFormSettings setMaxStackSizeFunction(ToIntFunction<IMaterial> maxStackSizeFunction) {
		this.maxStackSizeFunction = maxStackSizeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getMaxStackSizeFunction() {
		return maxStackSizeFunction;
	}

	@Override
	public IFluidFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction) {
		this.hasEffectFunction = hasEffectFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getHasEffectFunction() {
		return hasEffectFunction;
	}

	@Override
	public IFluidFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction) {
		this.burnTimeFunction = burnTimeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getBurnTimeFunction() {
		return burnTimeFunction;
	}
}
