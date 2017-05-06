package thelm.jaopca.api.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import thelm.jaopca.api.item.ItemBlockBase;

/**
 * Only Material and MapColor are implemented, use setCustomProperties for now.
 * @author TheLMiffy1111
 */
public class BlockProperties {

	public static final BlockProperties DEFAULT = new BlockProperties();

	//Might change some of these to BiFunction s
	public float hardness = 2F;
	public float resistance = 10F;
	public int lightOpacity = 255;
	public float lightValue = 0F;
	public SoundType soundType = SoundType.STONE;
	public Material material = Material.ROCK;
	public MapColor mapColor = MapColor.STONE;
	public float slipperiness = 0.6F;
	public boolean fallable = false;
	public Class<? extends BlockBase> blockClass = BlockBase.class;
	public Class<? extends ItemBlockBase> itemBlockClass = ItemBlockBase.class;

	public BlockProperties setHardness(float value) {
		hardness = value;
		return this;
	}

	public BlockProperties setResistance(float value) {
		resistance = value;
		return this;
	}

	public BlockProperties setLightOpacity(int value) {
		lightOpacity = value;
		return this;
	}

	public BlockProperties setLightValue(float value) {
		lightValue = value;
		return this;
	}

	public BlockProperties setSoundType(SoundType value) {
		soundType = value;
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

	public BlockProperties setSlipperiness(float value) {
		slipperiness = value;
		return this;
	}

	public BlockProperties setFallable(boolean value) {
		fallable = value;
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
