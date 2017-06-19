package thelm.jaopca.api.block;

import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.math.AxisAlignedBB;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ToFloatFunction;
import thelm.jaopca.api.item.ItemBlockBase;

/**
 * 
 * @author TheLMiffy1111
 */
public class BlockProperties {

	/**
	 * The default BlockProperties. DO NOT CALL ANY METHODS ON THIS FIELD.
	 */
	public static final BlockProperties DEFAULT = new BlockProperties();

	public ToFloatFunction<IOreEntry> hardnessFunc = (entry)->{return 2F;};
	public ToFloatFunction<IOreEntry> resisFunc = (entry)->{return hardnessFunc.applyAsFloat(entry)*5;};
	public ToIntFunction<IOreEntry> lgtOpacFunc = (entry)->{return 255;};
	public ToFloatFunction<IOreEntry> lgtValFunc = (entry)->{return 0F;};
	public ToFloatFunction<IOreEntry> slippyFunc = (entry)->{return 0.6F;};
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
	public Class<? extends BlockBase> blockClass = BlockBase.class;
	public Class<? extends ItemBlockBase> itemBlockClass = ItemBlockBase.class;

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

	public BlockProperties setMaterialMapColor(Material value) {
		material = value;
		mapColor = value.getMaterialMapColor();
		return this;
	}

	public BlockProperties setBlockClass(Class<? extends BlockBase> value) {
		blockClass = value;
		return this;
	}

	public BlockProperties setItemBlockClass(Class<? extends ItemBlockBase> value) {
		itemBlockClass = value;
		return this;
	}
}
