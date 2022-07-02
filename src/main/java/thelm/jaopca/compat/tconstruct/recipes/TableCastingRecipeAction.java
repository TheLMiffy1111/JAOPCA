package thelm.jaopca.compat.tconstruct.recipes;

import java.util.Objects;
import java.util.function.ToIntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.PreferenceCastingRecipe;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.compat.tconstruct.TConstructHelper;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class TableCastingRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object cast;
	public final Object input;
	public final int inputAmount;
	public final Object output;
	public final ToIntFunction<FluidStack> time;
	public final boolean consumeCast;
	public final boolean switchSlots;

	public TableCastingRecipeAction(ResourceLocation key, Object cast, Object input, int inputAmount, Object output, ToIntFunction<FluidStack> time, boolean consumeCast, boolean switchSlots) {
		this.key = Objects.requireNonNull(key);
		this.cast = cast;
		this.input = input;
		this.inputAmount = inputAmount;
		this.output = output;
		this.time = time;
		this.consumeCast = consumeCast;
		this.switchSlots = switchSlots;
	}

	@Override
	public boolean register() {
		FluidStack ing = MiscHelper.INSTANCE.getFluidStack(input, inputAmount);
		if(ing == null) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		RecipeMatch match = TConstructHelper.INSTANCE.getRecipeMatch(cast, 1, 1);
		if(output instanceof String) {
			if(!ApiImpl.INSTANCE.getOredict().contains(output)) {
				throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
			}
			TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe((String)output, match, ing, time.applyAsInt(ing), consumeCast, switchSlots));
		}
		else {
			ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, 1);
			if(stack.isEmpty()) {
				throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
			}
			TinkerRegistry.registerTableCasting(new CastingRecipe(stack, match, ing, time.applyAsInt(ing), consumeCast, switchSlots));
		}
		return true;
	}
}
