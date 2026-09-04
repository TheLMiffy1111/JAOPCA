package thelm.jaopca.compat.createmetallurgy.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid.MeltingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class MeltingRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object itemInput;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object secondFluidInput;
	public final int secondFluidInputAmount;
	public final Object itemOutput;
	public final int itemOutputCount;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final int time;
	public final int heatLevel;

	public MeltingRecipeSerializer(ResourceLocation key, Object itemInput, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		this(key, itemInput, FluidStack.EMPTY, 0, FluidStack.EMPTY, 0, ItemStack.EMPTY, 0, fluidOutput, fluidOutputAmount, time, heatLevel);
	}

	public MeltingRecipeSerializer(ResourceLocation key, Object itemInput, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		this(key, itemInput, FluidStack.EMPTY, 0, FluidStack.EMPTY, 0, itemOutput, itemOutputCount, fluidOutput, fluidOutputAmount, time, heatLevel);
	}

	public MeltingRecipeSerializer(ResourceLocation key, Object itemInput, Object fluidInput, int fluidInputAmount, Object secondFluidInput, int secondFluidInputAmount, Object itemOutput, int itemOutputCount, Object fluidOutput, int fluidOutputAmount, int time, int heatLevel) {
		this.key = Objects.requireNonNull(key);
		this.itemInput = itemInput;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.secondFluidInput = secondFluidInput;
		this.secondFluidInputAmount = secondFluidInputAmount;
		this.itemOutput = itemOutput;
		this.itemOutputCount = itemOutputCount;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.time = time;
		this.heatLevel = heatLevel;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(itemInput);
		SizedFluidIngredient fluidIng1 = MiscHelper.INSTANCE.getSizedFluidIngredient(fluidInput, fluidInputAmount);
		SizedFluidIngredient fluidIng2 = MiscHelper.INSTANCE.getSizedFluidIngredient(secondFluidInput, secondFluidInputAmount);
		if(ing == null && fluidIng1 == null && fluidIng2 == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+itemInput+", "+fluidInput+", "+secondFluidInput+", ");
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(itemOutput, itemOutputCount);
		FluidStack fluidStack = MiscHelper.INSTANCE.getFluidStack(fluidOutput, fluidOutputAmount);
		if(stack.isEmpty() && fluidStack.isEmpty()) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+itemOutput+", "+fluidOutput);
		}
		JsonArray resultJson = new JsonArray();
		if(!stack.isEmpty()) {
			resultJson.add(MiscHelper.INSTANCE.serializeItemStack(stack));
		}
		if(!fluidStack.isEmpty()) {
			resultJson.add(MiscHelper.INSTANCE.serializeFluidStack(fluidStack));
		}
		StandardProcessingRecipe.Builder<MeltingRecipe> builder = new StandardProcessingRecipe.Builder<>(MeltingRecipe::new, key);
		if(ing != null) {
			builder.require(ing);
		}
		if(fluidIng1 != null) {
			builder.require(fluidIng1);
		}
		if(fluidIng2 != null) {
			builder.require(fluidIng2);
		}
		if(!stack.isEmpty()) {
			builder.output(stack);
		}
		if(!fluidStack.isEmpty()) {
			builder.output(fluidStack);
		}
		builder.duration(time);
		builder.requiresHeat(switch(heatLevel) {
		default -> HeatCondition.NONE;
		case 1 -> HeatCondition.HEATED;
		case 2 -> HeatCondition.SUPERHEATED;
		});
		MeltingRecipe recipe = builder.build();
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
