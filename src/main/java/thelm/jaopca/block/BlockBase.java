package thelm.jaopca.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public class BlockBase extends Block {

	public final IOreEntry oreEntry;
	public final ItemEntry itemEntry;

	public BlockBase(ItemEntry itemEntry, IOreEntry oreEntry) {
		super(Material.ROCK);
		setUnlocalizedName("jaopca."+itemEntry.name);
		setRegistryName("jaopca:block_"+itemEntry.prefix+oreEntry.getOreName());
		this.oreEntry = oreEntry;
		this.itemEntry = itemEntry;
	}
}
