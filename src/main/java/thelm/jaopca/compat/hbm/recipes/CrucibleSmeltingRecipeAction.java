package thelm.jaopca.compat.hbm.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.hbm.HBMHelper;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CrucibleSmeltingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object[] output;

	public CrucibleSmeltingRecipeAction(String key, Object input, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean register() {
		List<Mats.MaterialStack> outputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer amount = MaterialShapes.INGOT.q(1);
			if(i < output.length && output[i] instanceof Integer) {
				amount = (Integer)output[i];
				++i;
			}
			Mats.MaterialStack stack = HBMHelper.INSTANCE.getMaterialStack(out, amount);
			if(stack == null) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(stack);
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		if(input instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(input)) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			Mats.materialOreEntries.put((String)input, outputs);
		}
		else {
			List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, true);
			if(ing.isEmpty()) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			for(ItemStack in : ing) {
				Mats.materialEntries.put(new RecipesCommon.ComparableStack(in), outputs);
			}
		}
		return true;
	}
}
