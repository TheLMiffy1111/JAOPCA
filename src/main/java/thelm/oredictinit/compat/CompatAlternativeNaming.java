package thelm.oredictinit.compat;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatAlternativeNaming implements ICompat {

	public static final CompatAlternativeNaming INSTANCE = new CompatAlternativeNaming();

	@Override
	public String getName() {
		return "alternativenaming";
	}

	//TODO add a config for this, meanwhile hardcode
	@Override
	public void register() {
		for(String name : OreDictionary.getOreNames()) {
			//Al
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
			//S
			if(name.contains("Sulfur")) {
				String replaced = name.replaceAll("Sulfur", "Sulphur");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			if(name.contains("Sulphur")) {
				String replaced = name.replaceAll("Sulphur", "Sulfur");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			//Cr
			if(name.contains("Chromium")) {
				String replaced = name.replaceAll("Chromium", "Chrome");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			if(name.contains("Chrome")) {
				String replaced = name.replaceAll("Chrome", "Chromium");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			//Cs
			if(name.contains("Caesium")) {
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					String replaced = name.replaceAll("Caesium", "Cesium");
					OreDictionary.registerOre(replaced, stk);
				}
			}
			if(name.contains("Cesium")) {
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					String replaced = name.replaceAll("Cesium", "Caesium");
					OreDictionary.registerOre(replaced, stk);
				}
			}
			//W
			if(name.contains("Wolfram")) {
				String replaced = name.replaceAll("Wolframium", "Tungsten").replaceAll("Wolfram", "Tungsten");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			//KNO3
			if(name.contains("Niter")) {
				String replaced = name.replaceAll("Niter", "Saltpeter");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			if(name.contains("Nitre")) {
				String replaced0 = name.replaceAll("Nitre", "Niter");
				String replaced1 = name.replaceAll("Nitre", "Saltpeter");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced0, stk);
					OreDictionary.registerOre(replaced1, stk);
				}
			}
			if(name.contains("Saltpeter")) {
				String replaced = name.replaceAll("Saltpeter", "Niter");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stk);
				}
			}
			if(name.contains("Saltpetre")) {
				String replaced0 = name.replaceAll("Saltpetre", "Niter");
				String replaced1 = name.replaceAll("Saltpetre", "Saltpeter");
				for(ItemStack stk : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced0, stk);
					OreDictionary.registerOre(replaced1, stk);
				}
			}
		}
	}
}
