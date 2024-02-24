package thelm.jaopca.compat.rotarycraft.recipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import gnu.trove.map.hash.TCustomHashMap;
import gnu.trove.set.hash.TCustomHashSet;
import gnu.trove.strategy.HashingStrategy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RotaryCraftRecipeHandler {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final HashingStrategy<ItemStack> HASH = new HashingStrategy<ItemStack>() {
		@Override
		public int computeHashCode(ItemStack s) {
			return s.getItem().hashCode();
		}
		@Override
		public boolean equals(ItemStack s1, ItemStack s2) {
			return s1.getItem() == s2.getItem() &&
					(s1.getItemDamage() == OreDictionary.WILDCARD_VALUE ||
					s2.getItemDamage() == OreDictionary.WILDCARD_VALUE ||
					s1.getItemDamage() == s2.getItemDamage());
		}
	};
	private static final List<JAOPCAExtractorRecipe> EXTRACTOR_RECIPES = new ArrayList<>();
	private static final Map<ItemStack, JAOPCAExtractorRecipe> EXTRACTOR_RECIPES_ORE = new TCustomHashMap<>(HASH);
	private static final Map<ItemStack, JAOPCAExtractorRecipe> EXTRACTOR_RECIPES_DUST = new TCustomHashMap<>(HASH);
	private static final Map<ItemStack, JAOPCAExtractorRecipe> EXTRACTOR_RECIPES_SLURRY = new TCustomHashMap<>(HASH);
	private static final Map<ItemStack, JAOPCAExtractorRecipe> EXTRACTOR_RECIPES_SOLUTION = new TCustomHashMap<>(HASH);
	private static final Map<ItemStack, JAOPCAExtractorRecipe> EXTRACTOR_RECIPES_FLAKES = new TCustomHashMap<>(HASH);

	public static boolean registerExtractorRecipe(Collection<ItemStack> ore, ItemStack dust, ItemStack slurry, ItemStack solution, ItemStack flakes, boolean isRare) {
		ModContainer modContainer = Loader.instance().activeModContainer();
		if(modContainer == null) {
			LOGGER.warn("External source tried to register JAOPCA RotaryCraft extractor recipe, ignoring");
			return false;
		}
		String modid = modContainer.getModId();
		if(!"jaopca".equals(modid)) {
			LOGGER.warn("External mod {} tried to register JAOPCA RotaryCraft extractor recipe, ignoring", new Object[] {modid});
			return false;
		}
		if(HASH.equals(dust, slurry) || HASH.equals(slurry, solution) || HASH.equals(solution, dust)) {
			return false;
		}
		Set<ItemStack> oreSet = new TCustomHashSet<>(HASH, ore);
		oreSet.remove(dust);
		oreSet.remove(slurry);
		oreSet.remove(solution);
		JAOPCAExtractorRecipe recipe = new JAOPCAExtractorRecipe(oreSet, dust, slurry, solution, flakes, isRare);
		EXTRACTOR_RECIPES.add(recipe);
		for(ItemStack o : oreSet) {
			EXTRACTOR_RECIPES_ORE.put(o, recipe);
		}
		EXTRACTOR_RECIPES_DUST.put(dust, recipe);
		EXTRACTOR_RECIPES_SLURRY.put(slurry, recipe);
		EXTRACTOR_RECIPES_SOLUTION.put(solution, recipe);
		EXTRACTOR_RECIPES_FLAKES.put(flakes, recipe);
		return true;
	}

	public static List<JAOPCAExtractorRecipe> getExtractorRecipes() {
		return Collections.unmodifiableList(EXTRACTOR_RECIPES);
	}

	public static JAOPCAExtractorRecipe getExtractorRecipe(ItemStack stack) {
		if(EXTRACTOR_RECIPES_ORE.containsKey(stack)) {
			return EXTRACTOR_RECIPES_ORE.get(stack);
		}
		if(EXTRACTOR_RECIPES_DUST.containsKey(stack)) {
			return EXTRACTOR_RECIPES_DUST.get(stack);
		}
		if(EXTRACTOR_RECIPES_SLURRY.containsKey(stack)) {
			return EXTRACTOR_RECIPES_SLURRY.get(stack);
		}
		if(EXTRACTOR_RECIPES_SOLUTION.containsKey(stack)) {
			return EXTRACTOR_RECIPES_SOLUTION.get(stack);
		}
		return null;
	}

	public static JAOPCAExtractorRecipe getExtractorRecipeFromResult(ItemStack stack) {
		if(EXTRACTOR_RECIPES_DUST.containsKey(stack)) {
			return EXTRACTOR_RECIPES_DUST.get(stack);
		}
		if(EXTRACTOR_RECIPES_SLURRY.containsKey(stack)) {
			return EXTRACTOR_RECIPES_SLURRY.get(stack);
		}
		if(EXTRACTOR_RECIPES_SOLUTION.containsKey(stack)) {
			return EXTRACTOR_RECIPES_SOLUTION.get(stack);
		}
		if(EXTRACTOR_RECIPES_FLAKES.containsKey(stack)) {
			return EXTRACTOR_RECIPES_FLAKES.get(stack);
		}
		return null;
	}

	public static ItemStack getExtractorResult(ItemStack stack) {
		if(EXTRACTOR_RECIPES_ORE.containsKey(stack)) {
			return EXTRACTOR_RECIPES_ORE.get(stack).dust.copy();
		}
		if(EXTRACTOR_RECIPES_DUST.containsKey(stack)) {
			return EXTRACTOR_RECIPES_DUST.get(stack).slurry.copy();
		}
		if(EXTRACTOR_RECIPES_SLURRY.containsKey(stack)) {
			return EXTRACTOR_RECIPES_SLURRY.get(stack).solution.copy();
		}
		if(EXTRACTOR_RECIPES_SOLUTION.containsKey(stack)) {
			return EXTRACTOR_RECIPES_SOLUTION.get(stack).flakes.copy();
		}
		return null;
	}

	public static boolean isExtractorOre(ItemStack stack) {
		return EXTRACTOR_RECIPES_ORE.containsKey(stack);
	}

	public static boolean isExtractorDust(ItemStack stack) {
		return EXTRACTOR_RECIPES_DUST.containsKey(stack);
	}

	public static boolean isExtractorSlurry(ItemStack stack) {
		return EXTRACTOR_RECIPES_SLURRY.containsKey(stack);
	}

	public static boolean isExtractorSolution(ItemStack stack) {
		return EXTRACTOR_RECIPES_SOLUTION.containsKey(stack);
	}
}
