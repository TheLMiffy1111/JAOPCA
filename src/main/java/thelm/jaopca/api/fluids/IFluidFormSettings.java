package thelm.jaopca.api.fluids;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface IFluidFormSettings extends IFormSettings {

	IFluidFormSettings setFluidCreator(IFluidCreator fluidCreator);

	IFluidCreator getFluidCreator();

	IFluidFormSettings setLuminosityFunction(ToIntFunction<IMaterial> luminosityFunction);

	ToIntFunction<IMaterial> getLuminosityFunction();

	IFluidFormSettings setDensityFunction(ToIntFunction<IMaterial> densityFunction);

	ToIntFunction<IMaterial> getDensityFunction();

	IFluidFormSettings setTemperatureFunction(ToIntFunction<IMaterial> temperatureFunction);

	ToIntFunction<IMaterial> getTemperatureFunction();

	IFluidFormSettings setViscosityFunction(ToIntFunction<IMaterial> viscosityFunction);

	ToIntFunction<IMaterial> getViscosityFunction();

	IFluidFormSettings setOpacityFunction(ToIntFunction<IMaterial> opacityFunction);

	ToIntFunction<IMaterial> getOpacityFunction();

	IFluidFormSettings setIsGaseousFunction(Predicate<IMaterial> isGaseousFunction);

	Predicate<IMaterial> getIsGaseousFunction();

	IFluidFormSettings setDisplayRarityFunction(Function<IMaterial, EnumRarity> displayRarityFunction);

	Function<IMaterial, EnumRarity> getDisplayRarityFunction();

	IFluidFormSettings setFluidBlockCreator(IFluidBlockCreator fluidBlockCreator);

	IFluidBlockCreator getFluidBlockCreator();

	IFluidFormSettings setMaxLevelFunction(ToIntFunction<IMaterial> maxLevelFunction);

	ToIntFunction<IMaterial> getMaxLevelFunction();

	IFluidFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction);

	Function<IMaterial, Material> getMaterialFunction();

	IFluidFormSettings setMapColorFunction(Function<IMaterial, MapColor> mapColorFunction);

	Function<IMaterial, MapColor> getMapColorFunction();

	IFluidFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction);

	ToDoubleFunction<IMaterial> getBlockHardnessFunction();

	IFluidFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction);

	ToDoubleFunction<IMaterial> getExplosionResistanceFunction();

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
}
