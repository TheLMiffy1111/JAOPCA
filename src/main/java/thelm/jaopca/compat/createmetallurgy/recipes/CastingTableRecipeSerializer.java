package thelm.jaopca.compat.createmetallurgy.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;

import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingOutput;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.base.CastingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import thelm.jaopca.api.recipes.IRecipeSerializer;
import thelm.jaopca.compat.createmetallurgy.CreateMetallurgyHelper;
import thelm.jaopca.utils.MiscHelper;

public class CastingTableRecipeSerializer implements IRecipeSerializer {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object mold;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final int outputCount;
	public final int time;
	public final boolean consumeMold;

	public CastingTableRecipeSerializer(ResourceLocation key, Object mold, Object input, int inputAmount, Object output, int outputCount, int time, boolean consumeMold) {
		this.key = Objects.requireNonNull(key);
		this.mold = mold;
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.outputCount = outputCount;
		this.time = time;
		this.consumeMold = consumeMold;
	}

	@Override
	public JsonElement get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(mold);
		SizedFluidIngredient fluidIng = MiscHelper.INSTANCE.getSizedFluidIngredient(input, inputAmount);
		if(fluidIng == null && ing == null) {
			throw new IllegalArgumentException("Empty ingredients in recipe "+key+": "+mold+", "+input);
		}
		CastingOutput out = CreateMetallurgyHelper.INSTANCE.getCastingOutput(output, outputCount);
		if(out == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		CastingRecipeBuilder builder = new CastingRecipeBuilder(CMRecipeTypes.CASTING_IN_TABLE, key);
		if(ing != null) {
			builder.require(ing);
		}
		if(fluidIng != null) {
			builder.require(fluidIng);
		}
		builder.output(out);
		builder.duration(time);
		CastingRecipe recipe = builder.build();
		return MiscHelper.INSTANCE.serializeRecipe(recipe);	
	}
}
