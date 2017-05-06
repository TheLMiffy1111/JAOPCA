package thelm.jaopca.oredictinit.compat;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.ICompat;

import static thelm.jaopca.oredictinit.registry.OreDictRegisCore.getBlock;
import static thelm.jaopca.oredictinit.registry.OreDictRegisCore.getItem;

public class CompatDraconicEvolution implements ICompat {

	@Override
	public void register() {
		OreDictionary.registerOre("dragonEgg", Blocks.DRAGON_EGG);

		if(Loader.isModLoaded("DragonMounts")) {
			Item egg = getItem("dragonmounts","dragon_egg");
			if(egg != null) {
				for(int i = 0; i < 8; i++) {
					OreDictionary.registerOre("dragonEgg", new ItemStack(egg, 1, i));
				}
			}
		}

		if(getBlock("draconicevolution","draconium_ore") != Blocks.AIR) {
			OreDictionary.registerOre("oreDraconium", new ItemStack(getBlock("draconicevolution","draconium_ore"), 1, 0));
			OreDictionary.registerOre("oreDraconium", new ItemStack(getBlock("draconicevolution","draconium_ore"), 1, 1));
			OreDictionary.registerOre("oreDraconium", new ItemStack(getBlock("draconicevolution","draconium_ore"), 1, 2));
		}
		if(getBlock("draconicevolution","draconium_block") != Blocks.AIR)
			OreDictionary.registerOre("blockDraconium", getBlock("draconicevolution","draconium_block"));
		if(getBlock("draconicevolution","draconic_block") != Blocks.AIR)
			OreDictionary.registerOre("blockDraconiumAwakened", getBlock("draconicevolution","draconic_block"));
		if(getItem("draconicevolution","draconium_ingot") != null)
			OreDictionary.registerOre("ingotDraconium", getItem("draconicevolution","draconium_ingot"));
		if(getItem("draconicevolution","draconium_dust") != null)
			OreDictionary.registerOre("dustDraconium", getItem("draconicevolution","draconium_dust"));
		if(getItem("draconicevolution","draconic_ingot") != null)
			OreDictionary.registerOre("ingotDraconiumAwakened", getItem("draconicevolution","draconic_ingot"));
		if(getItem("draconicevolution","nugget") != null) {
			OreDictionary.registerOre("nuggetDraconium", new ItemStack(getItem("draconicevolution","nugget"), 1, 0));
			OreDictionary.registerOre("nuggetDraconiumAwakened", new ItemStack(getItem("draconicevolution","nugget"), 1, 1));
		}
	}
}
