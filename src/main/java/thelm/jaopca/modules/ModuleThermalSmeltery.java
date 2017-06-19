package thelm.jaopca.modules;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;

public class ModuleThermalSmeltery extends ModuleBase {

	@Override
	public String getName() {
		return "thermalsmeltery";
	}

	@Override
	public void init() {
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
				if(item != null && rec.output != null && (JAOPCAApi.ITEMS_TABLE.containsValue(item.getItem()) || (item.getItem() instanceof ItemBlock && JAOPCAApi.BLOCKS_TABLE.containsValue(Block.getBlockFromItem(item.getItem()))) || JAOPCAApi.FLUIDS_TABLE.containsValue(rec.output.getFluid()))) {
					int energy = rec.getTemperature() * modifier;
					ModuleThermalExpansion.addCrucibleRecipe(energy, item, rec.output);
				}
			}
		}
	}
}
