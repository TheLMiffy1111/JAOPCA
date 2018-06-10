package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatFrogCraftRebirth implements ICompat {

	@Override
	public String getName() {
		return "frogcraftrebirth";
	}

	@Override
	public void register() {
		if(getItem("frogcraftrebirth", "metal_plate_dense") != null) {
			Item item = getItem("frogcraftrebirth", "metal_plate_dense");
			OreDictionary.registerOre("plateDenseAluminium", new ItemStack(item, 1, 0));
			OreDictionary.registerOre("plateDenseMagnalium", new ItemStack(item, 1, 1));
			OreDictionary.registerOre("plateDenseTitanium", new ItemStack(item, 1, 2));
		}
	}
}
