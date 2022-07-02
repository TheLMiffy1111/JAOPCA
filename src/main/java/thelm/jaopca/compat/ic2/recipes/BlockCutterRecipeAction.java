package thelm.jaopca.compat.ic2.recipes;

import java.util.Collections;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.ic2.IC2Helper;
import thelm.jaopca.utils.MiscHelper;

public class BlockCutterRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final int inputCount;
	public final Object output;
	public final int outputCount;
	public final int hardness;

	public BlockCutterRecipeAction(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, int hardness) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.output = output;
		this.outputCount = outputCount;
		this.hardness = hardness;
	}

	@Override
	public boolean register() {
		IRecipeInput ing = IC2Helper.INSTANCE.getRecipeInput(input, inputCount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		NBTTagCompound metadata = new NBTTagCompound();
		metadata.setInteger("hardness", hardness);
		return Recipes.blockcutter.addRecipe(ing, Collections.singletonList(stack), metadata, false);
	}
}
