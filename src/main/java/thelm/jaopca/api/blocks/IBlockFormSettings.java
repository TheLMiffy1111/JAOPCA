package thelm.jaopca.api.blocks;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.AxisAlignedBB;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface IBlockFormSettings extends IFormSettings {

	IBlockFormSettings setBlockCreator(IBlockCreator blockCreator);

	IBlockCreator getBlockCreator();

	IBlockFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction);

	Function<IMaterial, Material> getMaterialFunction();

	IBlockFormSettings setMapColorFunction(Function<IMaterial, MapColor> mapColorFunction);

	Function<IMaterial, MapColor> getMapColorFunction();

	IBlockFormSettings setBlocksMovement(boolean blocksMovement);

	boolean getBlocksMovement();

	IBlockFormSettings setSoundTypeFunction(Function<IMaterial, Block.SoundType> soundTypeFunction);

	Function<IMaterial, Block.SoundType> getSoundTypeFunction();

	IBlockFormSettings setLightOpacityFunction(ToIntFunction<IMaterial> lightOpacityFunction);

	ToIntFunction<IMaterial> getLightOpacityFunction();

	IBlockFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction);

	ToIntFunction<IMaterial> getLightValueFunction();

	IBlockFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction);

	ToDoubleFunction<IMaterial> getBlockHardnessFunction();

	IBlockFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction);

	ToDoubleFunction<IMaterial> getExplosionResistanceFunction();

	IBlockFormSettings setSlipperinessFunction(ToDoubleFunction<IMaterial> slipperinessFunction);

	ToDoubleFunction<IMaterial> getSlipperinessFunction();

	IBlockFormSettings setBoundingBox(AxisAlignedBB boundingBox);

	AxisAlignedBB getBoundingBox();

	IBlockFormSettings setHarvestToolFunction(Function<IMaterial, String> harvestToolFunction);

	Function<IMaterial, String> getHarvestToolFunction();

	IBlockFormSettings setHarvestLevelFunction(ToIntFunction<IMaterial> harvestLevelFunction);

	ToIntFunction<IMaterial> getHarvestLevelFunction();

	IBlockFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction);

	ToIntFunction<IMaterial> getFlammabilityFunction();

	IBlockFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction);

	ToIntFunction<IMaterial> getFireSpreadSpeedFunction();

	IBlockFormSettings setIsFireSourceFunction(Predicate<IMaterial> isFireSourceFunction);

	Predicate<IMaterial> getIsFireSourceFunction();

	IBlockFormSettings setIsBeaconBaseFunction(Predicate<IMaterial> isBeaconBaseFunction);

	Predicate<IMaterial> getIsBeaconBaseFunction();

	//IBlockFormSettings setIsFallableFunction(Predicate<IMaterial> isFallableFunction);

	//Predicate<IMaterial> getIsFallableFunction();

	IBlockFormSettings setBlockItemCreator(IBlockItemCreator blockItemCreator);

	IBlockItemCreator getBlockItemCreator();

	IBlockFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction);

	ToIntFunction<IMaterial> getItemStackLimitFunction();

	IBlockFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	IBlockFormSettings setDisplayRarityFunction(Function<IMaterial, EnumRarity> displayRarityFunction);

	Function<IMaterial, EnumRarity> getDisplayRarityFunction();
}
