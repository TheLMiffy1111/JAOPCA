package thelm.jaopca.compat.factorization.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import factorization.oreprocessing.TileEntitySlagFurnace;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class SlagFurnaceRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object firstOutput;
	public final float firstOutputChance;
	public final Object secondOutput;
	public final float secondOutputChance;

	public SlagFurnaceRecipeAction(String key, Object input, Object firstOutput, float firstOutputChance, Object secondOutput, float secondOutputChance) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.firstOutput = firstOutput;
		this.firstOutputChance = firstOutputChance;
		this.secondOutput = secondOutput;
		this.secondOutputChance = secondOutputChance;
	}

	@Override
	public boolean register() {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, true);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack1 = MiscHelper.INSTANCE.getItemStack(firstOutput, 1, false);
		if(stack1 == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+firstOutput);
		}
		ItemStack stack2 = MiscHelper.INSTANCE.getItemStack(secondOutput, 1, false);
		if(stack2 == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+secondOutput);
		}
		for(ItemStack in : ing) {
			TileEntitySlagFurnace.SlagRecipes.register(in, firstOutputChance, stack1, secondOutputChance, stack2);
		}
		return true;
	}
}
