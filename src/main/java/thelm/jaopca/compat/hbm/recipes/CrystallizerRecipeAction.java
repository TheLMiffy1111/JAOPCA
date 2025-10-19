package thelm.jaopca.compat.hbm.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.CrystallizerRecipes;
import com.hbm.inventory.RecipesCommon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CrystallizerRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object output;
	public final int count;

	public CrystallizerRecipeAction(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.count = count;
	}

	@Override
	public boolean register() {
		List<Object> ins = new ArrayList<>();
		if(itemInput instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(itemInput)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
			}
			ins.add(itemInput);
		}
		else {
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+itemInput);
			}
			for(ItemStack is : ing.getMatchingStacks()) {
				ins.add(new RecipesCommon.ComparableStack(is).singulize());
			}
		}
		FluidStack fluidIng = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
		if(fluidIng == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(Object in : ins) {
			CrystallizerRecipes.addRecipe(in, fluidIng, stack);
		}
		return true;
	}
}
