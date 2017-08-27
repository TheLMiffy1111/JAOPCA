package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getBlock;
import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatQuark implements ICompat {

	@Override
	public String getName() {
		return "quark";
	}

	@Override
	public void register() {
		if(getBlock("quark", "biotite_ore") != Blocks.AIR) {
			OreDictionary.registerOre("oreEnderBiotite", getBlock("quark", "biotite_ore"));
		}
		if(getItem("quark", "biotite") != null) {
			OreDictionary.registerOre("gemEnderBiotite", getItem("quark", "biotite"));
		}
	}
}
