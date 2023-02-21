package thelm.jaopca.compat.ic2.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.ic2.IC2Helper;
import thelm.jaopca.utils.MiscHelper;

public class CentrifugeRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int inputCount;
	public final int minHeat;
	public final Object[] output;

	public CentrifugeRecipeAction(String key, Object input, int inputCount, int minHeat, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.minHeat = minHeat;
		this.output = output;
	}

	@Override
	public boolean register() {
		IRecipeInput ing = IC2Helper.INSTANCE.getRecipeInput(input, inputCount);
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
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			outputs.add(stack);
		}
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("minHeat", minHeat);
		ItemStack[] outs = outputs.toArray(new ItemStack[outputs.size()]);
		Recipes.centrifuge.addRecipe(ing, metadata, outs);
		return true;
	}
}
