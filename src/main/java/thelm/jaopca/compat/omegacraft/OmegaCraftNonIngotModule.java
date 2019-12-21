package thelm.jaopca.compat.omegacraft;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "omegacraft")
public class OmegaCraftNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "diamond", "emerald", "lapis", "quartz"));

	private boolean recipeRegistered = false;
	
	@Override
	public String getName() {
		return "omegacraft_non_ingot";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}
	
	@Override
	public void onRecipeInjectComplete(IModuleData moduleData, IResourceManager resourceManager) {
		if(!recipeRegistered) {
			JAOPCAApi api = ApiImpl.INSTANCE;
			OmegaCraftHelper helper = OmegaCraftHelper.INSTANCE;
			IMiscHelper miscHelper = MiscHelper.INSTANCE;
			for(IMaterial material : moduleData.getMaterials()) {
				ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				helper.registerCrusherRecipe(oreLocation, 1, materialLocation, 2);
			}
			recipeRegistered = true;
		}
	}
}
