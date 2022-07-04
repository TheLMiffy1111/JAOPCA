package thelm.jaopca.compat.thaumcraft;

import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Ints;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thelm.jaopca.compat.thaumcraft.recipes.CrucibleRecipeAction;
import thelm.jaopca.compat.thaumcraft.recipes.SmeltingBonusRecipeAction;
import thelm.jaopca.compat.thaumcraft.recipes.SpecialMiningRecipeAction;
import thelm.jaopca.utils.ApiImpl;

public class ThaumcraftHelper {

	public static final ThaumcraftHelper INSTANCE = new ThaumcraftHelper();

	private ThaumcraftHelper() {}

	public boolean registerRecipesToResearch(String research, Collection<ResourceLocation> recipeKeys) {
		int stage = 0;
		String[] split = research.split("@(?=\\d+)");
		if(split.length == 2) {
			stage = Optional.ofNullable(Ints.tryParse(split[1])).orElse(0);
		}
		ResearchEntry researchEntry = ResearchCategories.getResearch(research);
		if(researchEntry == null) {
			return false;
		}
		ResearchStage[] stages = researchEntry.getStages();
		if(stage >= stages.length) {
			return false;
		}
		ResearchStage researchStage = stages[stage];
		researchStage.setRecipes(ArrayUtils.addAll(researchStage.getRecipes(), recipeKeys.toArray(new ResourceLocation[recipeKeys.size()])));
		return true;
	}

	public boolean registerCrucibleRecipe(ResourceLocation key, String researchReq, Object input, Object[] aspects, Object output, int count) {
		return ApiImpl.INSTANCE.registerRecipe(key, new CrucibleRecipeAction(key, researchReq, input, aspects, output, count));
	}

	public boolean registerSmeltingBonusRecipe(ResourceLocation key, Object input, Object output, int count, float chance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SmeltingBonusRecipeAction(key, input, output, count, chance));
	}

	public boolean registerSpecialMiningRecipe(ResourceLocation key, Object input, Object output, int count, float chance) {
		return ApiImpl.INSTANCE.registerRecipe(key, new SpecialMiningRecipeAction(key, input, output, count, chance));
	}
}
