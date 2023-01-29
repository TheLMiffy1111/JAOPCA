package thelm.jaopca.compat.ic2;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "ic2@[1.19,1.20)")
public class IC2NonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"coal", "diamond", "emerald", "lapis", "quartz", "redstone", "uranium"));

	@Override
	public String getName() {
		return "ic2_non_ingot";
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
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IC2Helper helper = IC2Helper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			if(material.getType() != MaterialType.DUST) {
				helper.registerExtractorRecipe(
						new ResourceLocation("jaopca", "ic2.ore_to_material."+material.getName()),
						oreLocation, 1,
						materialLocation, 3,
						1, 1, 1F);
			}
			else {
				helper.registerMaceratorRecipe(
						new ResourceLocation("jaopca", "ic2.ore_to_material."+material.getName()),
						oreLocation, 1,
						materialLocation, 5,
						1, 1, 0F);
			}
		}
	}
}
