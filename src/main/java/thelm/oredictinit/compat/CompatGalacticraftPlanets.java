package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getBlock;
import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatGalacticraftPlanets implements ICompat {

	@Override
	public String getName() {
		return "galacticraftplanets";
	}

	@Override
	public void register() {
		if(getItem("galacticraftplanets", "item_basic_mars") != null) {
			OreDictionary.registerOre("ingotDesh", new ItemStack(getItem("galacticraftplanets", "item_basic_mars"), 1, 2));
			OreDictionary.registerOre("compressedDesh", new ItemStack(getItem("galacticraftplanets", "item_basic_mars"), 1, 5));
		}
		if(getBlock("galacticraftplanets", "asteroids_block") != Blocks.AIR) {
			OreDictionary.registerOre("oreTitanium", new ItemStack(getBlock("galacticraftplanets", "asteroids_block"), 1, 4));
		}
		if(getBlock("galacticraftplanets", "mars") != Blocks.AIR) {
			OreDictionary.registerOre("blockDesh", new ItemStack(getBlock("galacticraftplanets", "mars"), 1, 8));
		}
	}
}
