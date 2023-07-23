package thelm.jaopca.blocks;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thelm.jaopca.api.blocks.IBlockCreator;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockItemCreator;
import thelm.jaopca.api.blocks.IBlockLootTableCreator;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;

class BlockFormSettings implements IBlockFormSettings {

	private IBlockCreator blockCreator = JAOPCABlock::new;
	private Function<IMaterial, MapColor> mapColorFunction = material->MapColor.METAL;
	//material->{
	//	int color = material.getColor();
	//	return Arrays.stream(MaterialColor.COLORS).filter(Objects::nonNull).
	//			min((matColor1, matColor2)->Integer.compare(
	//					MiscHelper.INSTANCE.squareColorDifference(color, matColor1.colorValue),
	//					MiscHelper.INSTANCE.squareColorDifference(color, matColor2.colorValue))).
	//			orElse(MaterialColor.IRON);
	//};
	private boolean blocksMovement = true;
	private boolean replaceable = false;
	private Function<IMaterial, SoundType> soundTypeFunction = material->SoundType.METAL;
	private ToIntFunction<IMaterial> lightValueFunction = material->0;
	private ToDoubleFunction<IMaterial> blockHardnessFunction = material->5;
	private ToDoubleFunction<IMaterial> explosionResistanceFunction = material->6;
	private ToDoubleFunction<IMaterial> frictionFunction = material->0.6;
	private boolean isFull = true;
	private VoxelShape shape = Shapes.block();
	private VoxelShape interactionShape = Shapes.empty();
	private Predicate<IMaterial> requiresToolFunction = material->false;
	private Function<IMaterial, String> harvestToolTagFunction = material->"minecraft:mineable/pickaxe";
	private Function<IMaterial, String> harvestTierTagFunction = material->"";
	private ToIntFunction<IMaterial> flammabilityFunction = material->0;
	private ToIntFunction<IMaterial> fireSpreadSpeedFunction = material->0;
	private Predicate<IMaterial> isFireSourceFunction = material->false;
	private Function<IMaterial, PushReaction> pushReactionFunction = material->PushReaction.NORMAL;
	private Function<IMaterial, NoteBlockInstrument> instrumentFunction = material->NoteBlockInstrument.HARP;
	private IBlockLootTableCreator blockLootTableCreator = (block, settings)->{
		return LootTable.lootTable().setParamSet(LootContextParamSets.BLOCK).
				withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).
						add(LootItem.lootTableItem(block.toBlock())).
						when(ExplosionCondition.survivesExplosion())).build();
	};
	private IBlockItemCreator itemBlockCreator = JAOPCABlockItem::new;
	private ToIntFunction<IMaterial> maxStackSizeFunction = material->64;
	private Predicate<IMaterial> hasEffectFunction = material->material.hasEffect();
	private Function<IMaterial, Rarity> displayRarityFunction = material->material.getDisplayRarity();
	private ToIntFunction<IMaterial> burnTimeFunction = material->-1;

	@Override
	public IFormType getType() {
		return BlockFormType.INSTANCE;
	}

	@Override
	public IBlockFormSettings setBlockCreator(IBlockCreator blockCreator) {
		this.blockCreator = blockCreator;
		return this;
	}

	@Override
	public IBlockCreator getBlockCreator() {
		return blockCreator;
	}

	@Override
	public IBlockFormSettings setMapColorFunction(Function<IMaterial, MapColor> mapColorFunction) {
		this.mapColorFunction = mapColorFunction;
		return this;
	}

	@Override
	public Function<IMaterial, MapColor> getMapColorFunction() {
		return mapColorFunction;
	}

	@Override
	public IBlockFormSettings setReplaceable(boolean replaceable) {
		this.replaceable = replaceable;
		return this;
	}

	@Override
	public boolean getReplaceable() {
		return replaceable;
	}

	@Override
	public IBlockFormSettings setBlocksMovement(boolean blocksMovement) {
		this.blocksMovement = blocksMovement;
		return this;
	}

	@Override
	public boolean getBlocksMovement() {
		return blocksMovement;
	}

	@Override
	public IBlockFormSettings setSoundTypeFunction(Function<IMaterial, SoundType> soundTypeFunction) {
		this.soundTypeFunction = soundTypeFunction;
		return this;
	}

	@Override
	public Function<IMaterial, SoundType> getSoundTypeFunction() {
		return soundTypeFunction;
	}

	@Override
	public IBlockFormSettings setLightValueFunction(ToIntFunction<IMaterial> lightValueFunction) {
		this.lightValueFunction = lightValueFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getLightValueFunction() {
		return lightValueFunction;
	}

	@Override
	public IBlockFormSettings setBlockHardnessFunction(ToDoubleFunction<IMaterial> blockHardnessFunction) {
		this.blockHardnessFunction = blockHardnessFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getBlockHardnessFunction() {
		return blockHardnessFunction;
	}

	@Override
	public IBlockFormSettings setExplosionResistanceFunction(ToDoubleFunction<IMaterial> explosionResistanceFunction) {
		this.explosionResistanceFunction = explosionResistanceFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getExplosionResistanceFunction() {
		return explosionResistanceFunction;
	}

	@Override
	public IBlockFormSettings setFrictionFunction(ToDoubleFunction<IMaterial> frictionFunction) {
		this.frictionFunction = frictionFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getFrictionFunction() {
		return frictionFunction;
	}

	@Override
	public IBlockFormSettings setShape(VoxelShape shape) {
		this.shape = shape;
		return this;
	}

	@Override
	public VoxelShape getShape() {
		return shape;
	}

	@Override
	public IBlockFormSettings setInteractionShape(VoxelShape interactionShape) {
		this.interactionShape = interactionShape;
		return this;
	}

	@Override
	public VoxelShape getInteractionShape() {
		return interactionShape;
	}

	@Override
	public IBlockFormSettings setRequiresToolFunction(Predicate<IMaterial> requiresToolFunction) {
		this.requiresToolFunction = requiresToolFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getRequiresToolFunction() {
		return requiresToolFunction;
	}

	@Override
	public IBlockFormSettings setHarvestToolTagFunction(Function<IMaterial, String> harvestToolTagFunction) {
		this.harvestToolTagFunction = harvestToolTagFunction;
		return this;
	}

	@Override
	public Function<IMaterial, String> getHarvestToolTagFunction() {
		return harvestToolTagFunction;
	}

	@Override
	public IBlockFormSettings setHarvestTierTagFunction(Function<IMaterial, String> harvestTierTagFunction) {
		this.harvestTierTagFunction = harvestTierTagFunction;
		return this;
	}

	@Override
	public Function<IMaterial, String> getHarvestTierTagFunction() {
		return harvestTierTagFunction;
	}

	@Override
	public IBlockFormSettings setFlammabilityFunction(ToIntFunction<IMaterial> flammabilityFunction) {
		this.flammabilityFunction = flammabilityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFlammabilityFunction() {
		return flammabilityFunction;
	}

	@Override
	public IBlockFormSettings setFireSpreadSpeedFunction(ToIntFunction<IMaterial> fireSpreadSpeedFunction) {
		this.fireSpreadSpeedFunction = fireSpreadSpeedFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getFireSpreadSpeedFunction() {
		return fireSpreadSpeedFunction;
	}

	@Override
	public IBlockFormSettings setIsFireSourceFunction(Predicate<IMaterial> isFireSourceFunction) {
		this.isFireSourceFunction = isFireSourceFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getIsFireSourceFunction() {
		return isFireSourceFunction;
	}

	@Override
	public IBlockFormSettings setPushReactionFunction(Function<IMaterial, PushReaction> pushReactionFunction) {
		this.pushReactionFunction = pushReactionFunction;
		return this;
	}

	@Override
	public Function<IMaterial, PushReaction> getPushReactionFunction() {
		return pushReactionFunction;
	}

	@Override
	public IBlockFormSettings setInstrumentFunction(Function<IMaterial, NoteBlockInstrument> instrumentFunction) {
		this.instrumentFunction = instrumentFunction;
		return this;
	}

	@Override
	public Function<IMaterial, NoteBlockInstrument> getInstrumentFunction() {
		return instrumentFunction;
	}

	@Override
	public IBlockFormSettings setBlockLootTableCreator(IBlockLootTableCreator blockLootTableCreator) {
		this.blockLootTableCreator = blockLootTableCreator;
		return this;
	}

	@Override
	public IBlockLootTableCreator getBlockLootTableCreator() {
		return blockLootTableCreator;
	}

	@Override
	public IBlockFormSettings setItemBlockCreator(IBlockItemCreator itemBlockCreator) {
		this.itemBlockCreator = itemBlockCreator;
		return this;
	}

	@Override
	public IBlockItemCreator getBlockItemCreator() {
		return itemBlockCreator;
	}

	@Override
	public IBlockFormSettings setMaxStackSizeFunction(ToIntFunction<IMaterial> maxStackSizeFunction) {
		this.maxStackSizeFunction = maxStackSizeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getMaxStackSizeFunction() {
		return maxStackSizeFunction;
	}

	@Override
	public IBlockFormSettings setHasEffectFunction(Predicate<IMaterial> hasEffectFunction) {
		this.hasEffectFunction = hasEffectFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getHasEffectFunction() {
		return hasEffectFunction;
	}

	@Override
	public IBlockFormSettings setDisplayRarityFunction(Function<IMaterial, Rarity> displayRarityFunction) {
		this.displayRarityFunction = displayRarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Rarity> getDisplayRarityFunction() {
		return displayRarityFunction;
	}

	@Override
	public IBlockFormSettings setBurnTimeFunction(ToIntFunction<IMaterial> burnTimeFunction) {
		this.burnTimeFunction = burnTimeFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getBurnTimeFunction() {
		return burnTimeFunction;
	}
}
