package thelm.jaopca.compat.railcraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "railcraft")
public class RailcraftNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Coal", "Diamond", "Emerald", "Lapis", "Niter", "Redstone", "Saltpeter", "Sulfur"));

	static {
		if(Loader.isModLoaded("forestry")) {
			Collections.addAll(BLACKLIST, "Apatite");
		}
	}

	@Override
	public String getName() {
		return "railcraft_non_ingot";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		RailcraftHelper helper = RailcraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			if(material.getType().isCrystalline()) {
				helper.registerRockCrusherRecipe(
						miscHelper.getRecipeKey("railcraft.ore_to_material", material.getName()),
						oreOredict, 100, new Object[] {
								materialOredict, 1, 1F,
								materialOredict, 1, 0.85F,
								materialOredict, 1, 0.25F,
						});
			}
			else {
				helper.registerRockCrusherRecipe(
						miscHelper.getRecipeKey("railcraft.ore_to_material", material.getName()),
						oreOredict, 100, new Object[] {
								materialOredict, 5, 1F,
								materialOredict, 1, 0.85F,
								materialOredict, 1, 0.35F,
						});
			}
		}
	}
}
