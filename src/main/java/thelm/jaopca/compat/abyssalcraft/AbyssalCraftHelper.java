package thelm.jaopca.compat.abyssalcraft;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.compat.abyssalcraft.recipes.CrystallizationRecipeAction;
import thelm.jaopca.compat.abyssalcraft.recipes.MaterializationRecipeAction;
import thelm.jaopca.compat.abyssalcraft.recipes.TransmutationRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class AbyssalCraftHelper implements IFuelHandler {

	public static final AbyssalCraftHelper INSTANCE = new AbyssalCraftHelper();
	private static final List<Pair<ItemStack, Integer>> FUELS = new ArrayList<>();

	private AbyssalCraftHelper() {}

	public boolean registerCrystal(Object crystal) {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(crystal, 1, true);
		if(!ing.isEmpty()) {
			for(ItemStack stack : ing) {
				AbyssalCraftAPI.addCrystal(stack);
			}
			return true;
		}
		return false;
	}

	public boolean registerFuel(Object fuel, int time) {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(fuel, 1, true);
		if(!ing.isEmpty()) {
			for(ItemStack stack : ing) {
				FUELS.add(Pair.of(stack, time));
			}
		}
		return false;
	}

	public boolean registerCrystallizationRecipe(String key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizationRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, experience));
	}

	public boolean registerCrystallizationRecipe(String key, Object input, Object output, int count, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizationRecipeAction(key, input, output, count, experience));
	}

	public boolean registerTransmutationRecipe(String key, Object input, Object output, int count, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new TransmutationRecipeAction(key, input, output, count, experience));
	}

	public boolean registerMaterializationRecipe(String key, Object output, int count, Object... input) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MaterializationRecipeAction(key, output, count, input));
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		for(Pair<ItemStack, Integer> pair : FUELS) {
			if(OreDictionary.itemMatches(pair.getLeft(), fuel, false)) {
				return pair.getRight();
			}
		}
		return 0;
	}
}
