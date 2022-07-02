package thelm.jaopca.compat.teslathingies.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.ndrei.teslapoweredthingies.machines.compoundmaker.CompoundMakerRecipe;
import net.ndrei.teslapoweredthingies.machines.compoundmaker.CompoundMakerRegistry;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class CompoundMakerRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object[] top;
	public final Object left;
	public final int leftAmount;
	public final Object[] bottom;
	public final Object right;
	public final int rightAmount;
	public final Object output;
	public final int outputCount;

	public CompoundMakerRecipeAction(ResourceLocation key, Object[] top, Object left, int leftAmount, Object output, int outputCount) {
		this(key, top, left, leftAmount, new Object[0], null, 0, output, outputCount);
	}

	public CompoundMakerRecipeAction(ResourceLocation key, Object[] top, Object[] bottom, Object output, int outputCount) {
		this(key, top, null, 0, bottom, null, 0, output, outputCount);
	}

	public CompoundMakerRecipeAction(ResourceLocation key, Object[] top, Object left, int leftAmount, Object[] bottom, Object output, int outputCount) {
		this(key, top, left, leftAmount, bottom, null, 0, output, outputCount);
	}

	public CompoundMakerRecipeAction(ResourceLocation key, Object[] top, Object left, int leftAmount, Object[] bottom, Object right, int rightAmount, Object output, int outputCount) {
		this.key = Objects.requireNonNull(key);
		this.top = top;
		this.left = left;
		this.leftAmount = leftAmount;
		this.bottom = bottom;
		this.right = right;
		this.rightAmount = rightAmount;
		this.output = output;
		this.outputCount = outputCount;
	}

	@Override
	public boolean register() {
		List<List<ItemStack>> topInputs = new ArrayList<>();
		int i = 0;
		while(i < top.length) {
			Object in = top[i];
			++i;
			Integer inc = 1;
			if(i < top.length && top[i] instanceof Integer) {
				inc = (Integer)top[i];
				++i;
			}
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == null || inc == 0) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			List<ItemStack> list = new ArrayList<>();
			for(ItemStack is : ing.getMatchingStacks()) {
				list.add(MiscHelper.INSTANCE.resizeItemStack(is, inc));
			}
			topInputs.add(list);
		}
		FluidStack leftStack = MiscHelper.INSTANCE.getFluidStack(left, leftAmount);
		List<List<ItemStack>> bottomInputs = new ArrayList<>();
		i = 0;
		while(i < bottom.length) {
			Object in = bottom[i];
			++i;
			Integer inc = 1;
			if(i < bottom.length && bottom[i] instanceof Integer) {
				inc = (Integer)bottom[i];
				++i;
			}
			Ingredient ing = MiscHelper.INSTANCE.getIngredient(in);
			if(ing == null || inc == 0) {
				throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+in);
			}
			List<ItemStack> list = new ArrayList<>();
			for(ItemStack is : ing.getMatchingStacks()) {
				list.add(MiscHelper.INSTANCE.resizeItemStack(is, inc));
			}
			bottomInputs.add(list);
		}
		FluidStack rightStack = MiscHelper.INSTANCE.getFluidStack(right, rightAmount);
		if(topInputs.isEmpty() && left == null && bottomInputs.isEmpty() && right == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+Arrays.deepToString(top)+", "+left+", "+Arrays.deepToString(bottom)+", "+right);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		for(List<ItemStack> topInput : Lists.cartesianProduct(topInputs)) {
			for(List<ItemStack> bottomInput : Lists.cartesianProduct(bottomInputs)) {
				CompoundMakerRegistry.INSTANCE.addRecipe(new CompoundMakerRecipe(key, stack,
						leftStack, topInput.toArray(new ItemStack[topInput.size()]),
						rightStack, bottomInput.toArray(new ItemStack[bottomInput.size()])), true);
			}
		}
		return true;
	}
}
