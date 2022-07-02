package thelm.jaopca.compat.teslathingies.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.ndrei.teslapoweredthingies.common.IRecipeOutput;
import net.ndrei.teslapoweredthingies.common.Output;
import net.ndrei.teslapoweredthingies.common.SecondaryOutput;
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRecipe;
import net.ndrei.teslapoweredthingies.machines.powdermaker.PowderMakerRegistry;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class PowderMakerRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object[] output;

	public PowderMakerRecipeAction(ResourceLocation key, Object input, int inputCount, Object... output) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
	}

	@Override
	public boolean register() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		List<ItemStack> ingList = Arrays.stream(ing.getMatchingStacks()).
				map(s->MiscHelper.INSTANCE.resizeItemStack(s, inputCount)).collect(Collectors.toList());
		List<IRecipeOutput> outputs = new ArrayList<>();
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
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(out, count);
			if(stack.isEmpty()) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
				continue;
			}
			if(chance == 1F) {
				outputs.add(new Output(stack));
			}
			else {
				outputs.add(new SecondaryOutput(chance, stack));
			}
		}
		if(outputs.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output));
		}
		PowderMakerRegistry.INSTANCE.addRecipe(new PowderMakerRecipe(key, ingList, outputs), true);
		return true;
	}
}
