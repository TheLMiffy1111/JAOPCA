package thelm.oredictinit.registry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.Woodchopper;
import thelm.oredictinit.api.ICompat;
import thelm.oredictinit.api.OreDictInitApi;
import thelm.oredictinit.lib.Data;

public class OreDictRegisCore {

	public static void initCompat() {
		for(ICompat compat : OreDictInitApi.ORE_DICT_COMPAT_LIST) {
			compat.register();
		}
	}

	public static void initCustom() {
		Woodchopper.info("Loading Custom Block Entries");
		for(String whole : Data.definedThingyBlocks) {
			for(String custom : whole.split(";")) {
				Woodchopper.debug("Trying to load block entry " + custom);
				String[] rawData = custom.trim().split(",");
				if(rawData.length == 4) {
					String[] entries = rawData[0].split("\\+");
					String[] damageValues = rawData[3].split("\\+");
					if(entries.length == damageValues.length) {
						for(int i = 0; i < entries.length; i++) {
							try {
								Integer.parseInt(damageValues[i]);
							}
							catch(Throwable e) {
								Woodchopper.warn("Entry " + custom + " has errored:");
								Woodchopper.error(e.toString());
								break;
							}
							addCustomEntryB(entries[i].trim(), rawData[1].trim(), rawData[2].trim(), damageValues[i]);
						}
					}
					else {
						Woodchopper.warn("Entry " + custom + " has errored:");
						Woodchopper.warn("Number of entries is inequal to number of damage values.");
					}
				}
				else if(rawData.length == 1 && rawData[0].trim().isEmpty());
				else {
					Woodchopper.warn("Entry " + custom + " has errored:");
					Woodchopper.warn("Entry length is incorrect.");
				}
			}
		}

		Woodchopper.info("Loading Custom Item Entries");
		for(String whole : Data.definedThingyItems) {
			for(String custom : whole.split(";")) {
				Woodchopper.debug("Trying to load item entry " + custom);
				String[] rawData = custom.trim().split(",");
				if(rawData.length == 4) {
					String[] entries = rawData[0].trim().split("\\+");
					String[] damageValues = rawData[3].trim().split("\\+");
					if(entries.length == damageValues.length) {
						for(int i = 0; i < entries.length; i++) {
							try {
								Integer.parseInt(damageValues[i]);
							}
							catch(Throwable e) {
								Woodchopper.warn("Entry " + custom + " has errored:");
								Woodchopper.error(e.toString());
								break;
							}
							addCustomEntryI(entries[i].trim(), rawData[1].trim(), rawData[2].trim(), damageValues[i].trim());
						}
					}
					else {
						Woodchopper.warn("Entry " + custom + " has errored:");
						Woodchopper.warn("Number of entries is inequal to number of damage values.");
					}
				}
				else if(rawData.length != 1 || rawData[0].trim().isEmpty()) {
					Woodchopper.warn("Entry " + custom + " has errored:");
					Woodchopper.warn("Entry length is incorrect.");
				}
			}
		}
	}

	public static void addCustomEntryB(String entry, String mod, String block, String damage) {
		int dam = Integer.parseInt(damage);
		Block thing = getBlock(mod,block);
		if(thing != Blocks.AIR)
			OreDictionary.registerOre(entry, new ItemStack(thing, 1, dam));
	}

	public static void addCustomEntryI(String entry, String mod, String item, String damage) {
		int dam = Integer.parseInt(damage);
		Item thing = getItem(mod,item);
		if(thing != null)
			OreDictionary.registerOre(entry, new ItemStack(thing, 1, dam));
	}

	public static Block getBlock(String mod, String block) {
		Block target = Block.REGISTRY.getObject(new ResourceLocation(mod, block));
		return target;
	}

	public static Item getItem(String mod, String item) {
		Item target = Item.REGISTRY.getObject(new ResourceLocation(mod, item));
		return target;
	}
}
