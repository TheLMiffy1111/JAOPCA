package thelm.jaopca.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public class CommonProxy {

	public void handleBlockRegister(ItemEntry itemEntry, IOreEntry oreEntry, Block block, ItemBlock itemblock) {}

	public void handleItemRegister(ItemEntry itemEntry, IOreEntry oreEntry, Item item) {}

	public void initItemColors() {}

	public void initBlockColors() {}
	
	public void initFluidColors() {}
}
