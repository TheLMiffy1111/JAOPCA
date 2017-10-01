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
				for(ItemStack stk : OreDictionary.getOres(name)) {
					OreDictionary.registerOre(name.
							replaceAll("Aluminium", "Aluminum"), stk);
				}
			}
			if(name.contains("Aluminum")) {
				for(ItemStack stk : OreDictionary.getOres(name)) {
					OreDictionary.registerOre(name.
							replaceAll("Aluminum", "Aluminium"), stk);
				}
			}
			//S: just in case
			if(name.contains("Sulphur")) {
				for(ItemStack stk : OreDictionary.getOres(name)) {
					OreDictionary.registerOre(name.
							replaceAll("Sulphur", "Sulfur"), stk);
				}
			}
			//Cr: why
			if(name.contains("Chrome")) {
				for(ItemStack stk : OreDictionary.getOres(name)) {
					OreDictionary.registerOre(name.
							replaceAll("Chrome", "Chromium"), stk);
				}
			}
			//Cs: just in case
			if(name.contains("Cesium")) {
				for(ItemStack stk : OreDictionary.getOres(name)) {
					OreDictionary.registerOre(name.
							replaceAll("Cesium", "Caesium"), stk);
				}
			}
			//W: just in case
			if(name.contains("Wolfram")) {
				for(ItemStack stk : OreDictionary.getOres(name)) {
					OreDictionary.registerOre(name.
							replaceAll("Wolframium", "Tungsten").
							replaceAll("Wolfram", "Tungsten"), stk);
				}
			}
		}
	}
}
