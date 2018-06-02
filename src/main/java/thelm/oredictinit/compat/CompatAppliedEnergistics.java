package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatAppliedEnergistics implements ICompat {

	@Override
	public String getName() {
		return "appliedenergistics";
	}

	@Override
	public void register() {
		if(getItem("appliedenergistics2", "material") != null) {
			Item material = getItem("appliedenergistics2", "material");
			OreDictionary.registerOre("gemCertusQuartz", new ItemStack(material, 1, 0));
			OreDictionary.registerOre("gemChargedCertusQuartz", new ItemStack(material, 1, 1));
			OreDictionary.registerOre("gemFluix", new ItemStack(material, 1, 7));
			if(true) {
				OreDictionary.registerOre("crystalCertusQuartz", new ItemStack(material, 1, 0));
				OreDictionary.registerOre("crystalChargedCertusQuartz", new ItemStack(material, 1, 1));
				OreDictionary.registerOre("crystalFluix", new ItemStack(material, 1, 7));

				OreDictionary.registerOre("crystalPureFluix", new ItemStack(material, 1, 12));
			}
		}
	}
}
