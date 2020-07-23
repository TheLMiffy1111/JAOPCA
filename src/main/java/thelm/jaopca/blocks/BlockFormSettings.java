package thelm.jaopca.blocks;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Rarity;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.ToolType;
import thelm.jaopca.api.blocks.IBlockCreator;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockItemCreator;
import thelm.jaopca.api.blocks.IBlockLootTableCreator;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.MiscHelper;

public class BlockFormSettings implements IBlockFormSettings {

	BlockFormSettings() {}

	private IBlockCreator blockCreator = JAOPCABlock::new;
	private Function<IMaterial, Material> materialFunction = material->Material.IRON;
	private Function<IMaterial, MaterialColor> materialColorFunction = materialFunction.andThen(Material::getColor);
	//material->{
	//	int color = material.getColor();
	//	return Arrays.stream(MaterialColor.COLORS).filter(Objects::nonNull).
	//			min((matColor1, matColor2)->Integer.compare(
	//					MiscHelper.INSTANCE.squareColorDifference(color, matColor1.colorValue),
	//					MiscHelper.INSTANCE.squareColorDifference(color, matColor2.colorValue))).
	//			orElse(MaterialColor.IRON);
	//};
	private boolean blocksMovement = true;
	private Function<IMaterial, SoundType> soundTypeFunction = material->SoundType.METAL;
	private ToIntFunction<IMaterial> lightValueFunction = material->0;
	private ToDoubleFunction<IMaterial> blockHardnessFunction = material->5;
	private ToDoubleFunction<IMaterial> explosionResistanceFunction = material->6;
	private ToDoubleFunction<IMaterial> slipperinessFunction = material->0.6;
	private boolean isFull = true;
	private VoxelShape shape = VoxelShapes.fullCube();
	private VoxelShape raytraceShape = VoxelShapes.empty();
	private Function<IMaterial, ToolType> harvestToolFunction = material->ToolType.PICKAXE;
	private ToIntFunction<IMaterial> harvestLevelFunction = material->0;
	private ToIntFunction<IMaterial> flammabilityFunction = material->0;
	private ToIntFunction<IMaterial> fireSpreadSpeedFunction = material->0;
	private Predicate<IMaterial> isFireSourceFunction = material->false;
	private IBlockLootTableCreator blockLootTableCreator = (block, settings)->{
		return LootTable.builder().setParameterSet(LootParameterSets.BLOCK).
				addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).
						addEntry(ItemLootEntry.builder(block.asBlock())).
						acceptCondition(SurvivesExplosion.builder())).build();
	};

	private IBlockItemCreator itemBlockCreator = JAOPCABlockItem::new;
	private ToIntFunction<IMaterial> itemStackLimitFunction = material->64;
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
	public IBlockFormSettings setMaterialFunction(Function<IMaterial, Material> materialFunction) {
		this.materialFunction = materialFunction;
		return this;
	}

	@Override
	public Function<IMaterial, Material> getMaterialFunction() {
		return materialFunction;
	}

	@Override
	public IBlockFormSettings setMaterialColorFunction(Function<IMaterial, MaterialColor> materialColorFunction) {
		this.materialColorFunction = materialColorFunction;
		return this;
	}

	@Override
	public Function<IMaterial, MaterialColor> getMaterialColorFunction() {
		return materialColorFunction;
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
	public IBlockFormSettings setSlipperinessFunction(ToDoubleFunction<IMaterial> slipperinessFunction) {
		this.slipperinessFunction = slipperinessFunction;
		return this;
	}

	@Override
	public ToDoubleFunction<IMaterial> getSlipperinessFunction() {
		return slipperinessFunction;
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
	public IBlockFormSettings setRaytraceShape(VoxelShape raytraceShape) {
		this.raytraceShape = raytraceShape;
		return this;
	}

	@Override
	public VoxelShape getRaytraceShape() {
		return raytraceShape;
	}

	@Override
	public IBlockFormSettings setHarvestToolFunction(Function<IMaterial, ToolType> harvestToolFunction) {
		this.harvestToolFunction = harvestToolFunction;
		return this;
	}

	@Override
	public Function<IMaterial, ToolType> getHarvestToolFunction() {
		return harvestToolFunction;
	}

	@Override
	public IBlockFormSettings setHarvestLevelFunction(ToIntFunction<IMaterial> harvestLevelFunction) {
		this.harvestLevelFunction = harvestLevelFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getHarvestLevelFunction() {
		return harvestLevelFunction;
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
	public IBlockFormSettings setItemStackLimitFunction(ToIntFunction<IMaterial> itemStackLimitFunction) {
		this.itemStackLimitFunction = itemStackLimitFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getItemStackLimitFunction() {
		return itemStackLimitFunction;
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
