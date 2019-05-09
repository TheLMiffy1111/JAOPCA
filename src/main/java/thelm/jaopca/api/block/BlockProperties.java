package thelm.jaopca.api.block;

import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.IProperties;
import thelm.jaopca.api.ToFloatFunction;
import thelm.jaopca.api.item.IItemBlockWithProperty;
import thelm.jaopca.api.item.ItemBlockBase;

/**
 *
 * @author TheLMiffy1111
 */
public class BlockProperties implements IProperties {

	/**
	 * The default BlockProperties. DO NOT CALL ANY METHODS ON THIS FIELD.
	 */
	public static final BlockProperties DEFAULT = new BlockProperties();

	public ToFloatFunction<IOreEntry> hardnessFunc = entry->2F;
	public ToFloatFunction<IOreEntry> resisFunc = entry->hardnessFunc.applyAsFloat(entry)*5;
	public ToIntFunction<IOreEntry> lgtOpacFunc = entry->255;
	public ToFloatFunction<IOreEntry> lgtValFunc = entry->0F;
	public ToFloatFunction<IOreEntry> slippyFunc = entry->0.6F;
	public Material material = Material.ROCK;
	public MapColor mapColor = MapColor.STONE;
	public SoundType soundType = SoundType.STONE;
	public int maxStkSize = 64;
	public EnumRarity rarity = EnumRarity.COMMON;
	public boolean fallable = false;
	public boolean beaconBase = false;
	public AxisAlignedBB boundingBox = Block.FULL_BLOCK_AABB;
	public String harvestTool = null;
	public int harvestLevel = -1;
	public boolean full = true;
	public boolean opaque = true;
	public BlockRenderLayer layer = BlockRenderLayer.TRANSLUCENT;
	public ToIntFunction<IOreEntry> flammabFunc = entry->0;
	public ToIntFunction<IOreEntry> fireSpdFunc = entry->0;
	public boolean fireSource = false;
	public Class<? extends IBlockWithProperty> blockClass = BlockBase.class;
	public Class<? extends IItemBlockWithProperty> itemBlockClass = ItemBlockBase.class;

	@Override
	public EnumEntryType getType() {
		return EnumEntryType.BLOCK;
	}

	public BlockProperties setHardnessFunc(ToFloatFunction<IOreEntry> value) {
		hardnessFunc = value;
		return this;
	}

	public BlockProperties setResistanceFunc(ToFloatFunction<IOreEntry> value) {
		resisFunc = value;
		return this;
	}

	public BlockProperties setLightOpacityFunc(ToIntFunction<IOreEntry> value) {
		lgtOpacFunc = value;
		return this;
	}

	public BlockProperties setLightValueFunc(ToFloatFunction<IOreEntry> value) {
		lgtValFunc = value;
		return this;
	}

	public BlockProperties setSlipperinessFunc(ToFloatFunction<IOreEntry> value) {
		slippyFunc = value;
		return this;
	}

	public BlockProperties setMaterial(Material value) {
		material = value;
		return this;
	}

	public BlockProperties setMapColor(MapColor value) {
		mapColor = value;
		return this;
	}

	public BlockProperties setSoundType(SoundType value) {
		soundType = value;
		return this;
	}

	public BlockProperties setMaxStackSize(int value) {
		maxStkSize = value;
		return this;
	}

	public BlockProperties setRarity(EnumRarity value) {
		rarity = value;
		return this;
	}

	public BlockProperties setFallable(boolean value) {
		fallable = value;
		return this;
	}

	public BlockProperties setBeaconBase(boolean value) {
		beaconBase = value;
		return this;
	}

	public BlockProperties setBoundingBox(AxisAlignedBB value) {
		boundingBox = value;
		return this;
	}

	public BlockProperties setHarvestTool(String value) {
		harvestTool = value;
		return this;
	}

	public BlockProperties setHarvestLevel(int value) {
		harvestLevel = value;
		return this;
	}

	public BlockProperties setFull(boolean value) {
		full = value;
		if(!value)
			setOpaque(value);
		return this;
	}

	public BlockProperties setOpaque(boolean value) {
		opaque = value;
		return this;
	}

	public BlockProperties setBlockLayer(BlockRenderLayer value) {
		layer = value;
		return this;
	}

	public BlockProperties setFlammabilityFunc(ToIntFunction<IOreEntry> value) {
		flammabFunc = value;
		return this;
	}

	public BlockProperties setFireSpreadSpeedFunc(ToIntFunction<IOreEntry> value) {
		fireSpdFunc = value;
		return this;
	}

	public BlockProperties setFireSource(boolean value) {
		fireSource = value;
		return this;
	}

	public BlockProperties setMaterialMapColor(Material value) {
		material = value;
		mapColor = value.getMaterialMapColor();
		return this;
	}

	public BlockProperties setBlockClass(Class<? extends IBlockWithProperty> value) {
		blockClass = value;
		return this;
	}

	public BlockProperties setItemBlockClass(Class<? extends IItemBlockWithProperty> value) {
		itemBlockClass = value;
		return this;
	}
}
