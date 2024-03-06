package thelm.jaopca.compat.modernindustrialization.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class CuttingMachineRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final int itemInputCount;
	public final float itemInputChance;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final float fluidInputChance;
	public final Object output;
	public final int outputCount;
	public final float outputChance;
	public final int eu;
	public final int duration;

	public CuttingMachineRecipeSerializer(ResourceLocation key, Object itemInput, int itemInputCount, float itemInputChance, Object fluidInput, int fluidInputAmount, float fluidInputChance, Object output, int outputCount, float outputChance, int eu, int duration) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.itemInputCount = itemInputCount;
		this.itemInputChance = itemInputChance;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.fluidInputChance = fluidInputChance;
		this.output = output;
		this.outputCount = outputCount;
		this.outputChance = outputChance;
		this.eu = eu;
		this.duration = duration;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		FluidStack fluidIng = MiscHelper.INSTANCE.getFluidStack(fluidInput, fluidInputAmount);
		if(ing == null && fluidIng.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+itemInput+", "+fluidInput);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		MachineRecipe recipe = new MIRecipeConstructor(MIMachineRecipeTypes.CUTTING_MACHINE, eu, duration).recipe();
		recipe.itemInputs.add(new MachineRecipe.ItemInput(ing, itemInputCount, itemInputChance));
		recipe.fluidInputs.add(new MachineRecipe.FluidInput(fluidIng.getFluid(), fluidInputAmount, fluidInputChance));
		recipe.itemOutputs.add(new MachineRecipe.ItemOutput(stack.getItem(), outputCount, outputChance));
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
