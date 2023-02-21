package thelm.jaopca.compat.abyssalcraft.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class MaterializationRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object output;
	public final int count;
	public final Object[] input;

	public MaterializationRecipeAction(String key, Object output, int count, Object... input) {
		this.key = Objects.requireNonNull(key);
		this.output = output;
		this.count = count;
		this.input = input;
	}

	@Override
	public boolean register() {
		List<ItemStack> out = MiscHelper.INSTANCE.getItemStacks(output, count, false);
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
			List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(in, inc, true);
			if(ing == null || inc == 0) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			List<ItemStack> list = new ArrayList<>();
			for(ItemStack is : ing) {
				if(APIUtils.isCrystal(is)) {
					list.add(is);
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
		for(List<ItemStack> in : MiscHelper.INSTANCE.guavaCartesianProduct(inputs)) {
			for(ItemStack stack : out) {
				AbyssalCraftAPI.addMaterialization(in.toArray(new ItemStack[in.size()]), stack);
			}
		}
		return true;
	}
}
