package thelm.jaopca.api.blocks;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;

public interface IBlockFormSettings extends IFormSettings {

	IBlockFormSettings setBlockCreator(IBlockCreator blockCreator);

	IBlockCreator getBlockCreator();

	IBlockFormSettings setMapColorFunction(Function<IMaterial, MapColor> mapColorFunction);

	Function<IMaterial, MapColor> getMapColorFunction();

	IBlockFormSettings setReplaceable(boolean replaceable);

	boolean getReplaceable();

	IBlockFormSettings setBlocksMovement(boolean blocksMovement);

	boolean getBlocksMovement();

	IBlockFormSettings setSoundTypeFunction(Function<IMaterial, SoundType> soundTypeFunction);

	Function<IMaterial, SoundType> getSoundTypeFunction();

	IBlockFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction);

	ToIntFunction<IMaterial> getLightValueFunction();

	IBlockFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction);

	ToDoubleFunction<IMaterial> getBlockHardnessFunction();

	IBlockFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction);

	ToDoubleFunction<IMaterial> getExplosionResistanceFunction();

	IBlockFormSettings setFrictionFunction(ToDoubleFunction<IMaterial> frictionFunction);

	ToDoubleFunction<IMaterial> getFrictionFunction();

	IBlockFormSettings setShape(VoxelShape shape);

	VoxelShape getShape();

	IBlockFormSettings setInteractionShape(VoxelShape interactionShape);

	VoxelShape getInteractionShape();

	IBlockFormSettings setRequiresToolFunction(Predicate<IMaterial> requiresToolFunction);

	Predicate<IMaterial> getRequiresToolFunction();

	IBlockFormSettings setHarvestToolTagFunction(Function<IMaterial, String> harvestToolTagFunction);

	Function<IMaterial, String> getHarvestToolTagFunction();

	IBlockFormSettings setHarvestTierTagFunction(Function<IMaterial, String> harvestTierTagFunction);

	Function<IMaterial, String> getHarvestTierTagFunction();

	IBlockFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction);

	ToIntFunction<IMaterial> getFlammabilityFunction();

	IBlockFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction);

	ToIntFunction<IMaterial> getFireSpreadSpeedFunction();

	IBlockFormSettings setIsFireSourceFunction(Predicate<IMaterial> isFireSourceFunction);

	Predicate<IMaterial> getIsFireSourceFunction();

	IBlockFormSettings setPushReactionFunction(Function<IMaterial, PushReaction> pushReactionFunction);

	Function<IMaterial, PushReaction> getPushReactionFunction();

	IBlockFormSettings setInstrumentFunction(Function<IMaterial, NoteBlockInstrument> instrumentFunction);

	Function<IMaterial, NoteBlockInstrument> getInstrumentFunction();

	//IBlockFormSettings setIsWaterLoggable(boolean waterLoggable);

	//boolean getIsWaterLoggable();

	//IBlockFormSettings setIsFallable(boolean fallable);

	//boolean getIsFallable();

	IBlockFormSettings setBlockLootTableCreator(IBlockLootTableCreator blockLootTableCreator);

	IBlockLootTableCreator getBlockLootTableCreator();

	IBlockFormSettings setItemBlockCreator(IBlockItemCreator itemBlockCreator);

	IBlockItemCreator getBlockItemCreator();

	IBlockFormSettings setMaxStackSizeFunction(ToIntFunction<IMaterial> maxStackSizeFunction);

	ToIntFunction<IMaterial> getMaxStackSizeFunction();

	IBlockFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction);

	Predicate<IMaterial> getHasEffectFunction();

	IBlockFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction);

	Function<IMaterial, Rarity> getDisplayRarityFunction();

	IBlockFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction);

	ToIntFunction<IMaterial> getBurnTimeFunction();
}
