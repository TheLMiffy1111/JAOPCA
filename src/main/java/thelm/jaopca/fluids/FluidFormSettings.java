package thelm.jaopca.fluids;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import thelm.jaopca.api.fluids.IBucketItemCreator;
import thelm.jaopca.api.fluids.IFluidAttributesCreator;
import thelm.jaopca.api.fluids.IFluidBlockCreator;
import thelm.jaopca.api.fluids.IFluidCreator;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

public class FluidFormSettings implements IFluidFormSettings {

	FluidFormSettings() {}

	private IFluidCreator fluidCreator = JAOPCAFluid::new;
	private ToIntFunction<IMaterial> maxLevelFunction = material->8;
	private ToIntFunction<IMaterial> tickRateFunction = material->5;
	private ToDoubleFunction<IMaterial> explosionResistanceFunction = material->100;
	private Predicate<IMaterial> canSourcesMultiplyFunction = material->false;
	private IFluidAttributesCreator fluidAttributesCreator = JAOPCAFluidAttributes::new;
	private Supplier<SoundEvent> fillSoundSupplier = ()->SoundEvents.BUCKET_FILL;
	private Supplier<SoundEvent> emptySoundSupplier = ()->SoundEvents.BUCKET_EMPTY;
	private ToIntFunction<IMaterial> densityFunction = material->1000;
	private ToIntFunction<IMaterial> viscosityFunction = material->tickRateFunction.applyAsInt(material)*200;
	private ToIntFunction<IMaterial> temperatureFunction = material->300;
	private Function<IMaterial, Rarity> displayRarityFunction = material->Rarity.COMMON;
	private IFluidBlockCreator fluidBlockCreator = JAOPCAFluidBlock::new;
	private ToIntFunction<IMaterial> levelDecreasePerBlockFunction = material->1;
	private Function<IMaterial, Material> materialFunction = material->Material.WATER;
	private Function<IMaterial, MaterialColor> materialColorFunction = materialFunction.andThen(Material::getColor);
	//material->{
	//	int color = material.getColor();
	//	return Arrays.stream(MaterialColor.COLORS).filter(Objects::nonNull).
	//			min((matColor1, matColor2)->Integer.compare(
	//					MiscHelper.INSTANCE.squareColorDifference(color, matColor1.colorValue),
	//					MiscHelper.INSTANCE.squareColorDifference(color, matColor2.colorValue))).
	//			orElse(MaterialColor.IRON);
	//};
	private ToIntFunction<IMaterial> lightValueFunction = material->0;
	private ToDoubleFunction<IMaterial> blockHardnessFunction = material->100;
	private ToIntFunction<IMaterial> flammabilityFunction = material->0;
	private ToIntFunction<IMaterial> fireSpreadSpeedFunction = material->0;
	private Predicate<IMaterial> isFireSourceFunction = material->false;
	private ToIntFunction<IMaterial> fireTimeFunction = material->-1;
	private IBucketItemCreator bucketItemCreator = JAOPCABucketItem::new;
	private ToIntFunction<IMaterial> itemStackLimitFunction = material->1;
	private Predicate<IMaterial> hasEffectFunction = material->material.hasEffect();
	private ToIntFunction<IMaterial> burnTimeFunction = material->-1;

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
	public IFluidFormSettings setCanSourcesMultiplyFunction(Predicate<IMaterial> canSourcesMultiplyFunction) {
		this.canSourcesMultiplyFunction = canSourcesMultiplyFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getCanSourcesMultiplyFunction() {
		return canSourcesMultiplyFunction;
	}

	@Override
	public IFluidFormSettings setFluidAttributesCreator(IFluidAttributesCreator fluidAttributesCreator) {
		this.fluidAttributesCreator = fluidAttributesCreator;
		return this;
	}

	@Override
	public IFluidAttributesCreator getFluidAttributesCreator() {
		return fluidAttributesCreator;
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
	public IFluidFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction) {
		this.materialFunction = materialFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Material> getMaterialFunction() {
		return materialFunction;
	}

	@Override
	public IFluidFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction) {
		this.materialColorFunction = materialColorFunction;
		return this;
	}

	@Override
	public Function<IMaterial, MaterialColor> getMaterialColorFunction() {
		return materialColorFunction;
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
	public IFluidFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction) {
		this.itemStackLimitFunction = itemStackLimitFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getItemStackLimitFunction() {
		return itemStackLimitFunction;
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
