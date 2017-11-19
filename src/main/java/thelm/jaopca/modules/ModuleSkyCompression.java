package thelm.jaopca.modules;

import java.util.List;

import com.google.common.collect.Lists;

import hqbanana.SkyCompression.AdditionalProcessRecipesManager;
import hqbanana.SkyCompression.init.ModBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import scala.Console;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleSkyCompression extends ModuleBase {

	@Override
	public String getName() {
		return "skycompression";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("skyresources");
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get("dirtyGem")) {
			float rarity = Utils.rarityReciprocalF(entry, 0.006D);
			ItemStack base = ModuleSkyResources.ORE_BASES.get(entry);
			AdditionalProcessRecipesManager.compressedRockGrinderRecipes.addRecipe(Utils.getOreStack("dirtyGem", entry, 1), rarity, base);
			Object compressed = getCompressed(base);
			if(base != compressed) {
				AdditionalProcessRecipesManager.compressedRockGrinderRecipes.addRecipe(Utils.getOreStack("dirtyGem", entry, 1), rarity*7F, compressed);
			}
		}
	}
	
	private static Object getCompressed(ItemStack parent) {
		Object obj = parent;
        if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.STONE))) {
        	obj = new ItemStack(ModBlocks.compressedStone);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.COBBLESTONE))) {
        	obj = "compressed1xCobblestone";
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.GRAVEL))) {
        	obj = "compressed1xGravel";
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.NETHERRACK))) {
        	obj = "compressed1xNetherrack";
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.END_STONE))) {
        	obj = new ItemStack(ModBlocks.compressedEndStone);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(com.bartz24.skyresources.registry.ModBlocks.dryCactus))) {
        	obj = new ItemStack(ModBlocks.compressedDryCactus);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.CACTUS))) {
        	obj = new ItemStack(ModBlocks.compressedCactus);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.DIRT))) {
        	obj = "compressed1xDirt";
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.CLAY))) {
        	obj = new ItemStack(ModBlocks.compressedClay);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.SAND))) {
        	obj = "compressed1xSand";
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.LOG, 1, 0))) {
        	obj = new ItemStack(ModBlocks.compressedLogOak);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.LOG, 1, 1))) {
        	obj = new ItemStack(ModBlocks.compressedLogSpruce);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.LOG, 1, 2))) {
        	obj = new ItemStack(ModBlocks.compressedLogBirch);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.LOG, 1, 3))) {
        	obj = new ItemStack(ModBlocks.compressedLogJungle);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.LOG2, 1, 0))) {
        	obj = new ItemStack(ModBlocks.compressedLogAcacia);
        }
        else if(ItemStack.areItemsEqual(parent, new ItemStack(Blocks.LOG2, 1, 1))) {
        	obj = new ItemStack(ModBlocks.compressedLogBigOak);
        }
        return obj;
    }
}
