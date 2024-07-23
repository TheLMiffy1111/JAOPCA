package thelm.jaopca.api.blocks;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface IBlockFormSettings extends IFormSettings {

	IBlockFormSettings setBlockCreator(IBlockCreator blockCreator);

	IBlockCreator getBlockCreator();

	IBlockFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction);

	Function<IMaterial, Material> getMaterialFunction();

	IBlockFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction);

	Function<IMaterial, MaterialColor> getMaterialColorFunction();

	IBlockFormSettings setBlocksMovement(boolean blocksMovement);

	boolean getBlocksMovement();

	IBlockFormSettings setSoundTypeFunction(Function<IMaterial, SoundType> soundTypeFunction);

	Function<IMaterial, SoundType> getSoundTypeFunction();

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

	IBlockFormSettings setShape(VoxelShape shape);

	VoxelShape getShape();

	IBlockFormSettings setRaytraceShape(VoxelShape raytraceShape);

	VoxelShape getRaytraceShape();

	IBlockFormSettings setRequiresToolFunction(Predicate<IMaterial> requiresToolFunction);

	Predicate<IMaterial> getRequiresToolFunction();

	IBlockFormSettings setHarvestToolFunction(Function<IMaterial, ToolType> harvestToolFunction);

	Function<IMaterial, ToolType> getHarvestToolFunction();

	IBlockFormSettings setHarvestLevelFunction(ToIntFunction<IMaterial> harvestLevelFunction);

	ToIntFunction<IMaterial> getHarvestLevelFunction();

	IBlockFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction);

	ToIntFunction<IMaterial> getFlammabilityFunction();

	IBlockFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction);

	ToIntFunction<IMaterial> getFireSpreadSpeedFunction();

	IBlockFormSettings setIsFireSourceFunction(Predicate<IMaterial> isFireSourceFunction);

	Predicate<IMaterial> getIsFireSourceFunction();

	IBlockFormSettings setBlockLootTableCreator(IBlockLootTableCreator blockLootTableCreator);

	IBlockLootTableCreator getBlockLootTableCreator();

	IBlockFormSettings setItemBlockCreator(IBlockItemCreator itemBlockCreator);

	IBlockItemCreator getBlockItemCreator();

	IBlockFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction);

	ToIntFunction<IMaterial> getItemStackLimitFunction();

	IBlockFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	IBlockFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction);

	Function<IMaterial, Rarity> getDisplayRarityFunction();

	IBlockFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();
}
