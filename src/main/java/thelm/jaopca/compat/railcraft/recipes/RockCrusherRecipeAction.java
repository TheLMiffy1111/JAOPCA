package thelm.jaopca.compat.railcraft.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mods.railcraft.api.crafting.IRockCrusherRecipe;
import mods.railcraft.api.crafting.RailcraftCraftingManager;
import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class RockCrusherRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object input;
	public final int time;
	public final Object[] output;

	public RockCrusherRecipeAction(String key, Object input, int time, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.time = time;
		this.output = output;
	}

	@Override
	public boolean register() {
		List<ItemStack> ing = MiscHelper.INSTANCE.getItemStacks(input, 1, true);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<Pair<ItemStack, Float>> outputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Float chance = 1F;
			if(i < output.length && output[i] instanceof Float) {
				chance = (Float)output[i];
				++i;
			}
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count, false);
			if(stack == null) {
				LOGGER.warn("Empty output in recipe {}: {}", new Object[] {key, out});
				continue;
			}
			outputs.add(Pair.of(stack, chance));
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		for(ItemStack in : ing) {
			IRockCrusherRecipe recipe = RailcraftCraftingManager.rockCrusher.createNewRecipe(in, true, false);
			for(Pair<ItemStack, Float> out : outputs) {
				recipe.addOutput(out.getLeft(), out.getRight());
			}
		}
		return true;
	}
}
