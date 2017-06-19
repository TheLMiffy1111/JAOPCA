package thelm.jaopca.modules;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.utils.Utils;

public class ModuleFuturePack extends ModuleBase {

	public static final HashMap<String,String> ORE_SECONDARIES = Maps.<String,String>newHashMap();

	@Override
	public String getName() {
		return "futurepack";
	}

	@Override
	public List<String> getDependencies() {
		return Lists.<String>newArrayList("dust");
	}

	@Override
	public List<String> getOreBlacklist() {
		return Lists.<String>newArrayList(
				"Iron", "Gold", "Tin", "Copper", "Zinc", "Magnetite", "Silver", "Lead", "Iridium", "Titanium", "Unobtainium", "Tungsten", "Platinum", "Cobalt", "Naquadah", "Nickel", "Molybdenum", "Beryllium", "Manganese", "Magnesium", "DevilsIron"
				);
	}

	@Override
	public void registerConfigs(Configuration config) {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			String value = config.get(Utils.to_under_score(entry.getOreName()), "futurePackSecondary", "minecraft:cobblestone@0x3").setRequiresMcRestart(true).getString();
			ORE_SECONDARIES.put(entry.getOreName(), value);
		}
	}

	@Override
	public void init() {
		for(IOreEntry entry : JAOPCAApi.MODULE_TO_ORES_MAP.get(this)) {
			for(ItemStack ore : OreDictionary.getOres("ore"+entry.getOreName())) {
				addCentrifugeRecipe(Utils.resizeStack(ore, 4), new ItemStack[] {
								Utils.getOreStack("dust", entry, 10),
								Utils.parseItemStack(ORE_SECONDARIES.get(entry.getOreName())),
								Utils.getOreStackExtra("dust", entry, 3) //Decided to go with 3
								}, Utils.energyI(entry, 6), Utils.energyI(entry, 200));
			}
		}
	}
	
	public static void addCentrifugeRecipe(ItemStack in, ItemStack[] out, int support, int time) {
		//FuturePack API makers, I don't know if you'll ever see this, BUT WHY DID YOU NOT PUT METHODS TO ADD CENTRIFUGE RECIPES?
		try {
			Class<?> managerClass = Class.forName("futurepack.common.crafting.FPZentrifugeManager");
			Class<?> recipeClass = Class.forName("futurepack.common.crafting.ZentrifugeRecipe");
			Field instanceField = managerClass.getField("instance");
			Method addRecipeMethod = managerClass.getMethod("addZentrifugeRecipe", ItemStack.class, ItemStack[].class, Integer.TYPE);
			Method setTimeMethod = recipeClass.getMethod("setTime", Integer.TYPE);
			Object instance = instanceField.get(null);
			Object recipe = addRecipeMethod.invoke(instance, in, out, support);
			setTimeMethod.invoke(recipe, time);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
