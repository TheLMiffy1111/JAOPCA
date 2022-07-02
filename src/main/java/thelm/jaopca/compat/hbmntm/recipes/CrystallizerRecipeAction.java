package thelm.jaopca.compat.hbmntm.recipes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hbm.inventory.CrystallizerRecipes;
import com.hbm.inventory.RecipesCommon;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class CrystallizerRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int count;

	public CrystallizerRecipeAction(ResourceLocation key, Object input, Object output, int count) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.count = count;
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
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, count);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		try {
			Field mapField = Arrays.stream(CrystallizerRecipes.class.getDeclaredFields()).
					filter(f->Map.class.isAssignableFrom(f.getType())).findFirst().get();
			mapField.setAccessible(true);
			Map<Object, ItemStack> map = (Map<Object, ItemStack>)mapField.get(null);
			for(Object in : ins) {
				map.put(in, stack);
			}
			return true;
		}
		catch(Exception e) {
			throw new IllegalStateException("Could not access shredder recipe map.");
		}
	}
}
