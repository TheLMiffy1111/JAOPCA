package thelm.jaopca.fluids;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.SoundEvent;
import thelm.jaopca.api.fluids.IFluidBlockCreator;
import thelm.jaopca.api.fluids.IFluidBlockModelMapCreator;
import thelm.jaopca.api.fluids.IFluidCreator;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.client.models.fluids.JAOPCAFluidBlockModelMapCreator;

public class FluidFormSettings implements IFluidFormSettings {

	FluidFormSettings() {}

	private IFluidCreator fluidCreator = JAOPCAFluid::new;
	private Supplier<SoundEvent> fillSoundSupplier = ()->SoundEvents.ITEM_BUCKET_FILL_LAVA;
	private Supplier<SoundEvent> emptySoundSupplier = ()->SoundEvents.ITEM_BUCKET_EMPTY_LAVA;
	private ToIntFunction<IMaterial> luminosityFunction = material->0;
	private ToIntFunction<IMaterial> densityFunction = material->1000;
	private ToIntFunction<IMaterial> temperatureFunction = material->300;
	private ToIntFunction<IMaterial> viscosityFunction = material->1000;
	private ToIntFunction<IMaterial> opacityFunction = material->255;
	private Predicate<IMaterial> isGaseousFunction = material->false;
	private Function<IMaterial, EnumRarity> displayRarityFunction = material->EnumRarity.COMMON;
	private IFluidBlockCreator fluidBlockCreator = JAOPCAFluidBlock::new;
	private ToIntFunction<IMaterial> maxLevelFunction = material->8;
	private Function<IMaterial, Material> materialFunction = material->Material.LAVA;
	private Function<IMaterial, MapColor> mapColorFunction = materialFunction.andThen(Material::getMaterialMapColor);
	private ToDoubleFunction<IMaterial> blockHardnessFunction = material->100;
	private ToDoubleFunction<IMaterial> explosionResistanceFunction = material->100;
	private ToIntFunction<IMaterial> flammabilityFunction = material->0;
	private ToIntFunction<IMaterial> fireSpreadSpeedFunction = material->0;
	private Predicate<IMaterial> isFireSourceFunction = material->false;
	private IFluidBlockModelMapCreator fluidBlockModelMapCreator = JAOPCAFluidBlockModelMapCreator.INSTANCE;

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
	public IFluidFormSettings setLuminosityFunction(ToIntFunction<IMaterial> luminosityFunction) {
		this.luminosityFunction = luminosityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getLuminosityFunction() {
		return luminosityFunction;
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
	public IFluidFormSettings setTemperatureFunction(ToIntFunction<IMaterial> temperatureFunction) {
		this.temperatureFunction = temperatureFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getTemperatureFunction() {
		return temperatureFunction;
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
	public IFluidFormSettings setOpacityFunction(ToIntFunction<IMaterial> opacityFunction) {
		this.opacityFunction = opacityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getOpacityFunction() {
		return opacityFunction;
	}

	@Override
	public IFluidFormSettings setIsGaseousFunction(Predicate<IMaterial> isGaseousFunction) {
		this.isGaseousFunction = isGaseousFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getIsGaseousFunction() {
		return isGaseousFunction;
	}

	@Override
	public IFluidFormSettings setDisplayRarityFunction(Function<IMaterial, EnumRarity> displayRarityFunction) {
		this.displayRarityFunction = displayRarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, EnumRarity> getDisplayRarityFunction() {
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
	public IFluidFormSettings setMaxLevelFunction(ToIntFunction<IMaterial> maxLevelFunction) {
		this.maxLevelFunction = maxLevelFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getMaxLevelFunction() {
		return maxLevelFunction;
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
	public IFluidFormSettings setMapColorFunction(Function<IMaterial, MapColor> mapColorFunction) {
		this.mapColorFunction = mapColorFunction;
		return this;
	}

	@Override
	public Function<IMaterial, MapColor> getMapColorFunction() {
		return mapColorFunction;
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
	public IFluidFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction) {
		this.explosionResistanceFunction = explosionResistanceFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getExplosionResistanceFunction() {
		return explosionResistanceFunction;
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
	public IFluidFormSettings setFluidBlockModelMapCreator(IFluidBlockModelMapCreator fluidBlockModelMapCreator) {
		this.fluidBlockModelMapCreator = fluidBlockModelMapCreator;
		return this;
	}

	@Override
	public IFluidBlockModelMapCreator getFluidBlockModelMapCreator() {
		return fluidBlockModelMapCreator;
	}
}
