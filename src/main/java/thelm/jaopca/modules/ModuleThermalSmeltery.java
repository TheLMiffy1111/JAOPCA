package thelm.jaopca.modules;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleAbstract;

public class ModuleThermalSmeltery extends ModuleAbstract {

	@Override
	public String getName() {
		return "thermalsmeltery";
	}

	@Override
	public List<ItemEntry> getItemRequests() {
		return Lists.<ItemEntry>newArrayList();
	}

	@Override
	public void registerRecipes() {
		int modifier = 5;
		try {
			Class<?> configClass = Class.forName("us.drullk.thermalsmeltery.common.TSmeltConfig");
			Field theField = configClass.getDeclaredField("rfCostMultiplier");
			theField.setAccessible(true);
			modifier = theField.getInt(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		for(MeltingRecipe rec : TinkerRegistry.getAllMeltingRecipies()) {
			for(ItemStack item : rec.input.getInputs()) {
				if(item != null && (JAOPCAApi.ITEMS_TABLE.values().contains(item.getItem()) || (item.getItem() instanceof ItemBlock && JAOPCAApi.BLOCKS_TABLE.values().contains(Block.getBlockFromItem(item.getItem()))))) {
					int energy = rec.getTemperature() * modifier;
					ModuleThermalExpansion.addCrucibleRecipe(energy, item, rec.getResult());
				}
			}
		}
	}
}
