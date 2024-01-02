package thelm.jaopca.compat.assemblylinemachines.recipes;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.haydenb.assemblylinemachines.block.machines.primitive.BlockHandGrinder;
import me.haydenb.assemblylinemachines.crafting.GrinderCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.utils.MiscHelper;

public class GrinderRecipeSupplier implements Supplier<GrinderCrafting> {

	private static final Logger LOGGER = LogManager.getLogger();

	public final ResourceLocation key;
	public final Object input;
	public final Object output;
	public final int outputCount;
	public final int grinds;
	public final int tier;
	public final boolean requiresMachine;

	public GrinderRecipeSupplier(ResourceLocation key, Object input, Object output, int outputCount, int grinds, int tier, boolean requiresMachine) {
		this.key = Objects.requireNonNull(key);
		this.input = input;
		this.output = output;
		this.outputCount = outputCount;
		this.grinds = grinds;
		this.tier = tier;
		this.requiresMachine = requiresMachine;
	}

	@Override
	public GrinderCrafting get() {
		Ingredient ing = MiscHelper.INSTANCE.getIngredient(input);
		if(ing.isEmpty()) {
			throw new IllegalArgumentException("Empty ingredient in recipe "+key+": "+input);
		}
		ItemStack stack = MiscHelper.INSTANCE.getItemStack(output, outputCount);
		if(stack.isEmpty()) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+output);
		}
		BlockHandGrinder.Blades blade;
		if(tier <= 0) {
			blade = BlockHandGrinder.Blades.TITANIUM;
		}
		else if(tier == 1) {
			blade = BlockHandGrinder.Blades.PUREGOLD;
		}
		else {
			blade = BlockHandGrinder.Blades.STEEL;
		}
		return new GrinderCrafting(key, ing, stack, grinds, blade, requiresMachine);
	}
}
