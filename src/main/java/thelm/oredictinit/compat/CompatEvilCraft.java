package thelm.oredictinit.compat;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;
import thelm.oredictinit.api.OreDictInitApi;

public class CompatEvilCraft implements ICompat {

	@Override
	public String getName() {
		return "evilcraft";
	}

	@Override
	public void register() {
		for(ItemStack item : OreDictionary.getOres("gemDarkCrushed")) {
			OreDictionary.registerOre("dustDark", item);
		}
		OreDictInitApi.addToJAOPCABlacklist("DarkCrushed");
	}
}
