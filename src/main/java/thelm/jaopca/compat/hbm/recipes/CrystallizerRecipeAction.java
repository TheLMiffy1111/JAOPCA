package thelm.jaopca.compat.hbm.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.FluidStack;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.recipes.CrystallizerRecipes;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.hbm.HBMHelper;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CrystallizerRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int outputCount;
	public final int time;

	public CrystallizerRecipeAction(String key, Object input, Object fluidInput, int fluidInputAmount, Object output, int outputCount, int time) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
	}

	@Override
	public boolean register() {
		List<Object> ins = new ArrayList<>();
		if(input instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(input)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			ins.add(input);
		}
		else {
			List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, true);
			if(ing.isEmpty()) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			for(ItemStack is : ing) {
				ins.add(new RecipesCommon.ComparableStack(is));
			}
		}
		FluidStack fluidIng = HBMHelper.INSTANCE.getFluidTypeStack(fluidInput, fluidInputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CrystallizerRecipes.CrystallizerRecipe rec = new CrystallizerRecipes.CrystallizerRecipe(stack, time);
		for(Object in : ins) {
			CrystallizerRecipes.registerRecipe(in, rec, fluidIng);
		}
		return true;
	}
}
