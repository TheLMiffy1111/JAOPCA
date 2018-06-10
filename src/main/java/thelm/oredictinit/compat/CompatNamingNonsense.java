package thelm.oredictinit.compat;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatNamingNonsense implements ICompat {

	@Override
	public String getName() {
		return "namingnonsense";
	}

	@Override
	public void register() {
		for(String name : OreDictionary.getOreNames()) {
			//Al: nonsense goes two ways
			if(name.contains("Aluminium")) {
				String replaced = name.replaceAll("Aluminium", "Aluminum");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			if(name.contains("Aluminum")) {
				String replaced = name.replaceAll("Aluminum", "Aluminium");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			//S: just in case
			if(name.contains("Sulphur")) {
				String replaced = name.replaceAll("Sulphur", "Sulfur");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			//Cr: why
			if(name.contains("Chrome")) {
				String replaced = name.replaceAll("Chrome", "Chromium");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			//Cs: just in case
			if(name.contains("Cesium")) {
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					String replaced = name.replaceAll("Cesium", "Caesium");
					OreDictionary.registerOre(replaced, stk);
				}
			}
			//W: just in case
			if(name.contains("Wolfram")) {
				String replaced = name.replaceAll("Wolframium", "Tungsten").replaceAll("Wolfram", "Tungsten");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
		}
	}
}
