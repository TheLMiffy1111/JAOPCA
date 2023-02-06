package thelm.jaopca.compat.astralsorcery;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "astralsorcery")
public class AstralSorceryModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aquamarine", "AstralStarmetal", "Coal", "Diamond", "Emerald", "Gold", "Iron", "Lapis", "Redstone"));

	@Override
	public String getName() {
		return "astralsorcery";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.GEM, MaterialType.CRYSTAL, MaterialType.DUST);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		AstralSorceryHelper helper = AstralSorceryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			int outputCount;
			switch(material.getType()) {
			case INGOT:
			default:
				outputCount = 3;
				break;
			case GEM:
			case CRYSTAL:
				outputCount = 4;
				break;
			case DUST:
				outputCount = 5;
				break;
			}
			helper.registerInfusionRecipe(
					miscHelper.getRecipeKey("astralsorcery.ore_to_material", material.getName()),
					oreOredict, materialOredict, outputCount,
					0.05F, false, true);
		}
	}
}
