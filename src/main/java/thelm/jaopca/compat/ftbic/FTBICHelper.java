package thelm.jaopca.compat.ftbic;

import java.util.Set;
import java.util.TreeSet;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.compat.ftbic.recipes.ExtrudingRecipeSerializer;
import thelm.jaopca.compat.ftbic.recipes.MaceratingRecipeSerializer;
import thelm.jaopca.compat.ftbic.recipes.RollingRecipeSerializer;
import thelm.jaopca.utils.ApiImpl;

public class FTBICHelper {

	public static final FTBICHelper INSTANCE = new FTBICHelper();

	private FTBICHelper() {}

	private final Set<String> blacklist = new TreeSet<>();

	public Set<String> getBlacklist() {
		if(blacklist.isEmpty()) {
			blacklist.addAll(FTBICConfig.MATERIALS.keySet());
		}
		return blacklist;
	}

	public void addMaterial(String material) {
		if(!FTBICConfig.MATERIALS.containsKey(material)) {
			FTBICConfig.addMaterial(material);
		}
	}

	public boolean registerMaceratingRecipe(ResourceLocation key, Object input, int inputCount, Object[] output, double time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new MaceratingRecipeSerializer(key, input, inputCount, output, time));
	}

	public boolean registerRollingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new RollingRecipeSerializer(key, input, inputCount, output, outputCount, time));
	}

	public boolean registerExtrudingRecipe(ResourceLocation key, Object input, int inputCount, Object output, int outputCount, double time) {
		return ApiImpl.INSTANCE.registerRecipe(key, new ExtrudingRecipeSerializer(key, input, inputCount, output, outputCount, time));
	}
}
