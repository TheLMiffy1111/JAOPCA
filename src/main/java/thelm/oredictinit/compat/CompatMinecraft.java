package thelm.oredictinit.compat;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatMinecraft implements ICompat {

	@Override
	public String getName() {
		return "minecraft";
	}

	@Override
	public void register() {
		OreDictionary.registerOre("gemCoal", new ItemStack(Items.COAL));
	}
}
