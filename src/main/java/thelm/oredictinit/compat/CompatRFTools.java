package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getBlock;
import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatRFTools implements ICompat {

	@Override
	public String getName() {
		return "rftools";
	}

	@Override
	public void register() {
		if(getBlock("rftools", "dimensional_shard_ore") != Blocks.AIR) {
			OreDictionary.registerOre("oreDimensionalShard", getBlock("rftools", "dimensional_shard_ore"));
		}
		if(getItem("rftools", "dimensional_shard") != null) {
			OreDictionary.registerOre("gemDimensionalShard", getItem("rftools", "dimensional_shard"));
		}
	}
}
