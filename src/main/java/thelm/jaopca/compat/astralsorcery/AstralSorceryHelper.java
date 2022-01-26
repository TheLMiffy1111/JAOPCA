package thelm.jaopca.compat.astralsorcery;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.compat.astralsorcery.recipes.InfuserRecipeSupplier;
import thelm.jaopca.utils.ApiImpl;

public class AstralSorceryHelper {

	public static final AstralSorceryHelper INSTANCE = new AstralSorceryHelper();

	private AstralSorceryHelper() {}

	public boolean registerInfuserRecipe(ResourceLocation key, Object liquidInput, Object itemInput, Object output, int outputCount, int time, float consumeChance, boolean consumeMultiple, boolean acceptChalice, boolean copyNBT) {
		return ApiImpl.INSTANCE.registerRecipe(key, new InfuserRecipeSupplier(key, liquidInput, itemInput, output, outputCount, time, consumeChance, consumeMultiple, acceptChalice, copyNBT));
	}
}
