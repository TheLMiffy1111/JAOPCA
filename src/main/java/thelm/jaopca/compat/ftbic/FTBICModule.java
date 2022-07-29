package thelm.jaopca.compat.ftbic;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "ftbic")
public class FTBICModule implements IModule {

	private static boolean addToFTBICMaterials = false;

	@Override
	public String getName() {
		return "ftbic";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return FTBICHelper.INSTANCE.getBlacklist();
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		config.getDefinedBoolean("recipes.addToFTBICMaterials", addToFTBICMaterials, "Should materials be directly added to FTBIC's material list to generate recipes instead of having JAOPCA do so.");
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		FTBICHelper helper = FTBICHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			if(!addToFTBICMaterials) {
				ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				helper.registerMaceratingRecipe(
						new ResourceLocation("jaopca", "ftbic.ore_to_dust."+material.getName()),
						oreLocation, 1, new Object[] {
								dustLocation, 2, 1D,
						}, 1D);
				if(material.getType() == MaterialType.INGOT) {
					ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
					helper.registerMaceratingRecipe(
							new ResourceLocation("jaopca", "ftbic.raw_material_to_dust."+material.getName()),
							rawMaterialLocation, 1, new Object[] {
									dustLocation, 2, 1D,
							}, 1D);
				}
			}
			else {
				helper.addMaterial(material.getName());
			}
		}
	}
}
