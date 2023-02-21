package thelm.jaopca.compat.rotarycraft.recipes;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import thelm.jaopca.api.recipes.IRecipeAction;
import thelm.jaopca.utils.MiscHelper;

public class ExtractorRecipeAction implements IRecipeAction {

	private static final Logger LOGGER = LogManager.getLogger();

	public final String key;
	public final Object ore;
	public final Object dust;
	public final Object slurry;
	public final Object solution;
	public final Object flakes;
	public final boolean isRare;

	public ExtractorRecipeAction(String key, Object ore, Object dust, Object slurry, Object solution, Object flakes, boolean isRare) {
		this.key = Objects.requireNonNull(key);
		this.ore = ore;
		this.dust = dust;
		this.slurry = slurry;
		this.solution = solution;
		this.flakes = flakes;
		this.isRare = isRare;
	}

	@Override
	public boolean register() {
		List<ItemStack> oreStacks = MiscHelper.INSTANCE.getItemStacks(ore, 1, true);
		if(oreStacks.isEmpty()) {
			throw new IllegalArgumentException("Empty input in recipe "+key+": "+ore);
		}
		ItemStack dustStack = MiscHelper.INSTANCE.getItemStack(dust, 1, false);
		if(dustStack == null) {
			throw new IllegalArgumentException("Empty intermediate in recipe "+key+": "+dust);
		}
		ItemStack slurryStack = MiscHelper.INSTANCE.getItemStack(slurry, 1, false);
		if(slurryStack == null) {
			throw new IllegalArgumentException("Empty intermediate in recipe "+key+": "+slurry);
		}
		ItemStack solutionStack = MiscHelper.INSTANCE.getItemStack(solution, 1, false);
		if(solutionStack == null) {
			throw new IllegalArgumentException("Empty intermediate in recipe "+key+": "+solution);
		}
		ItemStack flakesStack = MiscHelper.INSTANCE.getItemStack(flakes, 1, false);
		if(flakesStack == null) {
			throw new IllegalArgumentException("Empty output in recipe "+key+": "+flakes);
		}
		return RotaryCraftRecipeHandler.registerExtractorRecipe(oreStacks, dustStack, slurryStack, solutionStack, flakesStack, isRare);
	}
}
