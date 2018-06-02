package thelm.jaopca.api.block;

import java.util.Arrays;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.utils.JAOPCAStateMap;

public abstract class BlockMetaBase extends BlockBase {

	public final PropertyInteger meta = PropertyInteger.create("meta", 0, getMaxMeta());
	public String[] prefixes = new String[getMaxMeta()];

	public BlockMetaBase(Material material, MapColor mapColor, ItemEntry itemEntry, IOreEntry oreEntry) {
		super(material, mapColor, itemEntry, oreEntry);
		setDefaultState(getBlockState().getBaseState().withProperty(meta, 0));
		Arrays.fill(prefixes, itemEntry.prefix);
	}

	public String[] getPrefixes() {
		return prefixes;
	}

	@Override
	public String getPrefix(int meta) {
		return hasSubtypes() ? getPrefixes()[meta] : super.getPrefix(meta);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(this.meta, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(meta);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return hasSubtypes() ? getMetaFromState(state) : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, meta);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomStateMapper(this, new JAOPCAStateMap.Builder(new ResourceLocation(getItemEntry().itemModelLocation.toString().split("#")[0])).build());
	}
}
