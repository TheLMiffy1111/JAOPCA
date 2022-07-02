package thelm.jaopca.compat.abyssalcraft;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.event.FuelBurnTimeEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thelm.jaopca.compat.abyssalcraft.recipes.CrystallizationRecipeAction;
import thelm.jaopca.compat.abyssalcraft.recipes.MaterializationRecipeAction;
import thelm.jaopca.compat.abyssalcraft.recipes.TransmutationRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class AbyssalCraftHelper {

	public static final AbyssalCraftHelper INSTANCE = new AbyssalCraftHelper();
	private static final List<Pair<Ingredient, Integer>> FUELS = new ArrayList<>();

	private AbyssalCraftHelper() {}

	public boolean registerCrystal(Object crystal) {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(crystal);
		if(ing != null) {
			for(ItemStack stack : ing.getMatchingStacks()) {
				AbyssalCraftAPI.addCrystal(stack);
			}
			return true;
		}
		return false;
	}

	public boolean registerFuel(Object fuel, int time) {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(fuel);
		if(ing != null) {
			FUELS.add(Pair.of(ing, time));
		}
		return false;
	}

	public boolean registerCrystallizationRecipe(ResourceLocation key, Object input, Object output, int outputCount, Object secondOutput, int secondOutputCount, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizationRecipeAction(key, input, output, outputCount, secondOutput, secondOutputCount, experience));
	}

	public boolean registerCrystallizationRecipe(ResourceLocation key, Object input, Object output, int count, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrystallizationRecipeAction(key, input, output, count, experience));
	}

	public boolean registerTransmutationRecipe(ResourceLocation key, Object input, Object output, int count, float experience) {
		return ApiImpl.INSTANCE.registerRecipe(key, new TransmutationRecipeAction(key, input, output, count, experience));
	}

	public boolean registerMaterializationRecipe(ResourceLocation key, Object output, int count, Object... input) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MaterializationRecipeAction(key, output, count, input));
	}

	@SubscribeEvent
	public void onFuelBurnTime(FuelBurnTimeEvent event) {
		ItemStack stack = event.getItemStack();
		for(Pair<Ingredient, Integer> pair : FUELS) {
			if(pair.getLeft().apply(stack)) {
				event.setBurnTime(pair.getRight());
				break;
			}
		}
	}
}
