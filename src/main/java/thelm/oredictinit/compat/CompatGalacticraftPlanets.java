package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getBlock;
import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import java.lang.reflect.Method;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

public class CompatGalacticraftPlanets implements ICompat {

	@Override
	public String getName() {
		return "galacticraftplanets";
	}

	@Override
	public void register() {
		try {
			Class<?> marsBlocksClass = Class.forName("micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks");
			Method initMethod = marsBlocksClass.getDeclaredMethod("oreDictRegistration");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(getItem("galacticraftplanets", "item_basic_mars") != null) {
			OreDictionary.registerOre("ingotDesh", new ItemStack(getItem("galacticraftplanets", "item_basic_mars"), 1, 2));
			OreDictionary.registerOre("compressedDesh", new ItemStack(getItem("galacticraftplanets", "item_basic_mars"), 1, 5));
		}

		try {
			Class<?> asteroidBlocksClass = Class.forName("micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks");
			Method initMethod = asteroidBlocksClass.getDeclaredMethod("oreDictRegistration");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			Class<?> asteroidItemsClass = Class.forName("micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidItems");
			Method initMethod = asteroidItemsClass.getDeclaredMethod("oreDictRegistrations");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(getBlock("galacticraftplanets", "asteroids_block") != Blocks.AIR) {
			OreDictionary.registerOre("oreTitanium", new ItemStack(getBlock("galacticraftplanets", "asteroids_block"), 1, 4));
		}

		try {
			Class<?> venusBlocksClass = Class.forName("micdoodle8.mods.galacticraft.planets.venus.blocks.VenusBlocks");
			Method initMethod = venusBlocksClass.getDeclaredMethod("oreDictRegistration");
			initMethod.setAccessible(true);
			initMethod.invoke(null);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(getItem("galacticraftplanets", "basic_item_venus") != null) {
			OreDictionary.registerOre("ingotLead", new ItemStack(getItem("galacticraftplanets", "basic_item_venus"), 1, 1));
			OreDictionary.registerOre("dustSolar", new ItemStack(getItem("galacticraftplanets", "basic_item_venus"), 1, 4));
		}
	}
}
