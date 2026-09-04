package thelm.jaopca.compat.neovitae.recipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.recipe.athanor.AthanorRecipe;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.utils.MiscHelper;

public class AthanorSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final Identifier key;
	public final Object input;
	public final int inputCount;
	public final Object tool;
	public final Object fluidInput;
	public final int fluidInputAmount;
	public final Object[] output;
	public final Object fluidOutput;
	public final int fluidOutputAmount;
	public final Map<SpiritusType, Double> spiritusCosts;
	public final boolean spiritusBoost;

	public AthanorSerializer(Identifier key, Object input, int inputCount, Object tool, Object[] output, Map<SpiritusType, Double> spiritusCosts, boolean spiritusBoost) {
		this(key, input, inputCount, tool, null, 0, output, null, 0, spiritusCosts, spiritusBoost);
	}

	public AthanorSerializer(Identifier key, Object input, int inputCount, Object tool, Object fluidInput, int fluidInputAmount, Object[] output, Object fluidOutput, int fluidOutputAmount, Map<SpiritusType, Double> spiritusCosts, boolean spiritusBoost) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.inputCount = inputCount;
		this.tool = tool;
		this.fluidInput = fluidInput;
		this.fluidInputAmount = fluidInputAmount;
		this.output = output;
		this.fluidOutput = fluidOutput;
		this.fluidOutputAmount = fluidOutputAmount;
		this.spiritusCosts = spiritusCosts;
		this.spiritusBoost = spiritusBoost;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		Ingredient ingTool = MiscHelper.INSTANCE.getIngredient(tool);
		SizedFluidIngredient fluidIng = MiscHelper.INSTANCE.getSizedFluidIngredient(fluidInput, fluidInputAmount);
		List<ItemStackTemplate> guaranteedOutputs = new ArrayList<>();
		List<Pair<ItemStackTemplate, Double>> chanceOutputs = new ArrayList<>();
		int i = 0;
		while(i < output.length) {
			Object out = output[i];
			++i;
			Integer count = 1;
			if(i < output.length && output[i] instanceof Integer) {
				count = (Integer)output[i];
				++i;
			}
			Double chance = 1D;
			if(i < output.length && output[i] instanceof Double) {
				chance = (Double)output[i];
				++i;
			}
			ItemStackTemplate stack = MiscHelper.INSTANCE.getItemStackTemplate(out, count);
			if(stack == null) {
				LOGGER.warn("Empty output in recipe {}: {}", key, out);
			}
			if(chance == 1) {
				guaranteedOutputs.add(stack);
			}
			else {
				chanceOutputs.add(Pair.of(stack, chance));
			}
		}
		FluidStackTemplate fluidResult = MiscHelper.INSTANCE.getFluidStackTemplate(fluidOutput, fluidOutputAmount);
		if(guaranteedOutputs.isEmpty() && chanceOutputs.isEmpty() && fluidResult == null) {
			throw new IllegalArgumentException("Empty outputs in recipe "+key+": "+Arrays.deepToString(output)+", "+fluidOutput);
		}
		AthanorRecipe recipe = new AthanorRecipe(Optional.ofNullable(ingTool), List.of(ing), guaranteedOutputs, chanceOutputs, Optional.ofNullable(fluidIng), Optional.ofNullable(fluidResult), spiritusCosts, spiritusBoost);
		return MiscHelper.INSTANCE.serializeRecipe(recipe);
	}
}
