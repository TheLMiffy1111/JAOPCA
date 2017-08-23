package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getBlock;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.ICompat;

public class CompatGalacticraftCore implements ICompat {

	@Override
	public String getName() {
		return "galacticraftcore";
	}

	@Override
	public void register() {
		if(getBlock("galacticraftcore", "fallen_meteor") != Blocks.AIR) {
			OreDictionary.registerOre("oreMeteoricIron", getBlock("galacticraftcore", "fallen_meteor"));
		}
		if(getBlock("galacticraftcore", "basic_block_core") != Blocks.AIR) {
			OreDictionary.registerOre("blockMeteoricIron", new ItemStack(getBlock("galacticraftcore", "basic_block_core"), 1, 12));
		}
	}
}
