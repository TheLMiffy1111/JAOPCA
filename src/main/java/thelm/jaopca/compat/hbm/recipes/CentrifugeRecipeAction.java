package thelm.jaopca.compat.hbm.recipes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.CentrifugeRecipes;
import com.hbm.inventory.RecipesCommon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object[] output;

	public CentrifugeRecipeAction(ResourceLocation key, Object input, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
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
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
			if(ing == null) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
			}
			for(ItemStack is : ing.getMatchingStacks()) {
				ins.add(new RecipesCommon.ComparableStack(is).singulize());
			}
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
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
			Map<Object, ItemStack[]> map = (Map<Object, ItemStack[]>)mapField.get(null);
			for(Object in : ins) {
				map.put(in, out);
			}
			return true;
		}
		catch(Exception e) {
			throw new IllegalStateException("Could not access centrifuge recipe map.");
		}
	}
}
