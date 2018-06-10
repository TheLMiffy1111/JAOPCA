package thelm.oredictinit.compat;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatGregTech implements ICompat {

	@Override
	public String getName() {
		return "gregtech";
	}

	@Override
	public void register() {
		try {
			Class<?> oreDictLoaderClass = Class.forName("gregtech.loaders.load.OreDictionaryLoader");
			Method initMethod = oreDictLoaderClass.getDeclaredMethod("init");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			Class<?> itemClass = Class.forName("gregtech.common.items.MetaItems");
			Method oreDictMethod = itemClass.getDeclaredMethod("registerOreDict");
			oreDictMethod.setAccessible(true);
			oreDictMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			Class<?> blockClass = Class.forName("gregtech.common.blocks.MetaBlocks");
			Method oreDictMethod = blockClass.getDeclaredMethod("registerOreDict");
			oreDictMethod.setAccessible(true);
			oreDictMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		String replaced;
		for(String name : OreDictionary.getOreNames()) {
			if(name.startsWith("oreBasalt") && name.length() > 9) {
				replaced = name.replaceFirst("Basalt", "");
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
			if(name.startsWith("oreBlackgranite") && name.length() > 15) {
				replaced = name.replaceFirst("Blackgranite", "");
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
			if(name.startsWith("oreEndstone") && name.length() > 11) {
				replaced = name.replaceFirst("Endstone", "");
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
			if(name.startsWith("oreGravel") && name.length() > 9) {
				replaced = name.replaceFirst("Gravel", "");
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
			if(name.startsWith("oreNetherrack") && name.length() > 13) {
				replaced = name.replaceFirst("Netherrack", "");
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
			if(name.startsWith("oreMarble") && name.length() > 9) {
				replaced = name.replaceFirst("Marble", "");
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
			if(name.startsWith("oreRedgranite") && name.length() > 13) {
				replaced = name.replaceFirst("Redgranite", "");
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
			if(name.startsWith("oreSand") && name.length() > 7) {
				replaced = name.replaceFirst("Sand", "");
				for(ItemStack stack : OreDictionary.getOres(name, false)) {
					OreDictionary.registerOre(replaced, stack);
				}
			}
		}
	}
}
