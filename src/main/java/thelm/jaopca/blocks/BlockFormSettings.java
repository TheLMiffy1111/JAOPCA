package thelm.jaopca.blocks;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.math.AxisAlignedBB;
import thelm.jaopca.api.blocks.IBlockCreator;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockItemCreator;
import thelm.jaopca.api.blocks.IBlockItemModelFunctionCreator;
import thelm.jaopca.api.blocks.IBlockModelMapCreator;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.client.models.blocks.JAOPCABlockItemModelFunctionCreator;
import thelm.jaopca.client.models.blocks.JAOPCABlockModelMapCreator;

public class BlockFormSettings implements IBlockFormSettings {

	BlockFormSettings() {}

	private IBlockCreator blockCreator = JAOPCABlock::new;
	private Function<IMaterial, Material> materialFunction = material->Material.IRON;
	private Function<IMaterial, MapColor> mapColorFunction = materialFunction.andThen(Material::getMaterialMapColor);
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
	private ToIntFunction<IMaterial> lightOpacityFunction = material->15;
	private ToIntFunction<IMaterial> lightValueFunction = material->0;
	private ToDoubleFunction<IMaterial> blockHardnessFunction = material->5;
	private ToDoubleFunction<IMaterial> explosionResistanceFunction = material->6;
	private ToDoubleFunction<IMaterial> slipperinessFunction = material->0.6;
	private AxisAlignedBB boundingBox = Block.FULL_BLOCK_AABB;
	private Function<IMaterial, String> harvestToolFunction = material->"";
	private ToIntFunction<IMaterial> harvestLevelFunction = material->-1;
	private ToIntFunction<IMaterial> flammabilityFunction = material->0;
	private ToIntFunction<IMaterial> fireSpreadSpeedFunction = material->0;
	private Predicate<IMaterial> isFireSourceFunction = material->false;
	private Predicate<IMaterial> isBeaconBaseFunction = material->false;
	private IBlockModelMapCreator blockModelMapCreator = JAOPCABlockModelMapCreator.INSTANCE;
	private IBlockItemCreator blockItemCreator = JAOPCABlockItem::new;
	private ToIntFunction<IMaterial> itemStackLimitFunction = material->64;
	private Predicate<IMaterial> hasEffectFunction = material->material.hasEffect();
	private Function<IMaterial, EnumRarity> displayRarityFunction = material->material.getDisplayRarity();
	private ToIntFunction<IMaterial> burnTimeFunction = material->-1;
	private IBlockItemModelFunctionCreator blockItemModelFunctionCreator = JAOPCABlockItemModelFunctionCreator.INSTANCE;

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
	public IBlockFormSettings setMapColorFunction(Function<IMaterial, MapColor> mapColorFunction) {
		this.mapColorFunction = mapColorFunction;
		return this;
	}

	@Override
	public Function<IMaterial, MapColor> getMapColorFunction() {
		return mapColorFunction;
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
	public IBlockFormSettings setLightOpacityFunction(ToIntFunction<IMaterial> lightOpacityFunction) {
		this.lightOpacityFunction = lightOpacityFunction;
		return this;
	}

	@Override
	public ToIntFunction<IMaterial> getLightOpacityFunction() {
		return lightOpacityFunction;
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
	public IBlockFormSettings setBoundingBox(AxisAlignedBB boundingBox) {
		this.boundingBox = boundingBox;
		return this;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	@Override
	public IBlockFormSettings setHarvestToolFunction(Function<IMaterial, String> harvestToolFunction) {
		this.harvestToolFunction = harvestToolFunction;
		return this;
	}

	@Override
	public Function<IMaterial, String> getHarvestToolFunction() {
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
	public IBlockFormSettings setIsBeaconBaseFunction(Predicate<IMaterial> isBeaconBaseFunction) {
		this.isBeaconBaseFunction = isBeaconBaseFunction;
		return this;
	}

	@Override
	public Predicate<IMaterial> getIsBeaconBaseFunction() {
		return isBeaconBaseFunction;
	}

	@Override
	public IBlockFormSettings setBlockModelMapCreator(IBlockModelMapCreator blockModelMapCreator) {
		this.blockModelMapCreator = blockModelMapCreator;
		return this;
	}

	@Override
	public IBlockModelMapCreator getBlockModelMapCreator() {
		return blockModelMapCreator;
	}

	@Override
	public IBlockFormSettings setBlockItemCreator(IBlockItemCreator blockItemCreator) {
		this.blockItemCreator = blockItemCreator;
		return this;
	}

	@Override
	public IBlockItemCreator getBlockItemCreator() {
		return blockItemCreator;
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
	public IBlockFormSettings setDisplayRarityFunction(Function<IMaterial, EnumRarity> displayRarityFunction) {
		this.displayRarityFunction = displayRarityFunction;
		return this;
	}

	@Override
	public Function<IMaterial, EnumRarity> getDisplayRarityFunction() {
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

	@Override
	public IBlockFormSettings setBlockItemModelFunctionCreator(IBlockItemModelFunctionCreator blockItemModelFunctionCreator) {
		this.blockItemModelFunctionCreator = blockItemModelFunctionCreator;
		return this;
	}

	@Override
	public IBlockItemModelFunctionCreator getBlockItemModelFunctionCreator() {
		return blockItemModelFunctionCreator;
	}
}
