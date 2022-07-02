package thelm.jaopca.compat.abyssalcraft.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class MaterializationRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object output;
	public final int count;
	public final Object[] input;

	public MaterializationRecipeAction(ResourceLocation key, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.output = output;
		this.count = count;
		this.input = input;
	}

	@Override
	public boolean register() {
		Ingredient out = MiscHelper.INSTANCE.getIngredient(output);
		if(out == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		List<List<ItemStack>> inputs = new ArrayList<>();
		int i = 0;
		while(i < input.length) {
			Object in = input[i];
			++i;
			Integer inc = 1;
			if(i < input.length && input[i] instanceof Integer) {
				inc = (Integer)input[i];
				++i;
			}
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == null || inc == 0) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			List<ItemStack> list = new ArrayList<>();
			for(ItemStack is : ing.getMatchingStacks()) {
				if(APIUtils.isCrystal(is)) {
					list.add(MiscHelper.INSTANCE.resizeItemStack(is, inc));
				}
			}
			if(list.isEmpty()) {
				throw new IllegalArgumentException("Non-crystal ingredient in recipe "+key+": "+in);
			}
			inputs.add(list);
		}
		if(inputs.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(input));
		}
		for(List<ItemStack> in : Lists.cartesianProduct(inputs)) {
			for(ItemStack stack : out.getMatchingStacks()) {
				AbyssalCraftAPI.addMaterialization(MiscHelper.INSTANCE.resizeItemStack(stack, count), in.toArray(new ItemStack[in.size()]));
			}
		}
		return true;
	}
}
