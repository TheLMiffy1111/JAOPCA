package thelm.jaopca.compat.hbm.recipes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.recipes.CentrifugeRecipes;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.hbm.HBMHelper;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final Object[] output;

	public CentrifugeRecipeAction(String key, Object input, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean register() {
		RecipesCommon.AStack ing = HBMHelper.INSTANCE.getAStack(input, 1);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> outputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count, false);
			if(stack == null) {
				LOGGER.warn("Empty output in recipe {}: {}", new Object[] {key, out});
				continue;
			}
			outputs.add(stack);
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		ItemStack[] out = outputs.toArray(new ItemStack[outputs.size()]);
		try {
			Field mapField = Arrays.stream(CentrifugeRecipes.class.getDeclaredFields()).
					filter(f->Map.class.isAssignableFrom(f.getType())).findFirst().get();
			mapField.setAccessible(true);
			Map<RecipesCommon.AStack, ItemStack[]> map = (Map<RecipesCommon.AStack, ItemStack[]>)mapField.get(null);
			map.put(ing, out);
			return true;
		}
		catch(Exception e) {
			throw new IllegalStateException("Could not access shredder recipe map.");
		}
	}
}
