package thelm.jaopca.compat.factorization.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import factorization.oreprocessing.TileEntityGrinder;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class GrinderRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object output;
	public final float chance;

	public GrinderRecipeAction(String key, Object input, Object output, float chance) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.chance = chance;
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
			ins.addAll(ing);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, 1, false);
		if(stack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(Object in : ins) {
			TileEntityGrinder.addRecipe(in, stack, chance);
		}
		return true;
	}
}
