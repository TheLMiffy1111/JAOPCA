package thelm.jaopca.compat.astralsorcery.recipes;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.infusion.InfusionRecipeRegistry;
import hellfirepvp.astralsorcery.common.crafting.infusion.recipes.BasicInfusionRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.astralsorcery.AstralSorceryHelper;
import thelm.jaopca.utils.MiscHelper;

public class InfusionRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final float consumptionChance;
	public final boolean consumeMultiple;
	public final boolean acceptsChalices;

	public InfusionRecipeAction(ResourceLocation key, Object input, Object output, int outputCount, float consumptionChance, boolean consumeMultiple, boolean acceptsChalices) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.consumptionChance = consumptionChance;
		this.consumeMultiple = consumeMultiple;
		this.acceptsChalices = acceptsChalices;
	}

	@Override
	public boolean register() {
		ItemHandle ing = AstralSorceryHelper.INSTANCE.getItemHandle(input);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BasicInfusionRecipe recipe = new BasicInfusionRecipe(stack, ing);
		recipe.setLiquidStarlightConsumptionChance(consumptionChance);
		if(consumeMultiple) {
			recipe.setConsumeMultiple();
		}
		recipe.setCanBeSupportedByChalices(acceptsChalices);
		return InfusionRecipeRegistry.registerInfusionRecipe(recipe) != null;
	}
}
