package thelm.jaopca.compat.actuallyadditions;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "actuallyadditions")
public class ActuallyAdditionsNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"black_quartz", "coal", "diamond", "emerald", "lapis", "redstone"));

	@Override
	public String getName() {
		return "actuallyadditions_non_ingot";
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
		ActuallyAdditionsHelper helper = ActuallyAdditionsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
			if(material.hasExtra(1)) {
				IMaterial extraMaterial = material.getExtra(1);
				ResourceLocation extraLocation = miscHelper.getTagLocation(
						switch(extraMaterial.getType()) {
						case INGOT -> "raw_materials";
						case INGOT_LEGACY -> "dusts";
						default -> extraMaterial.getType().getFormName();
						}, extraMaterial.getName());
				if(itemTags.contains(extraLocation)) {
					helper.registerCrushingRecipe(
							miscHelper.getRecipeKey("actuallyadditions.ore_to_material", material.getName()),
							oreLocation, materialLocation, material.getType() != MaterialType.DUST ? 2 : 5, 1F, extraLocation, 1, 0.2F);
				}
				continue;
			}
			helper.registerCrushingRecipe(
					miscHelper.getRecipeKey("actuallyadditions.ore_to_material", material.getName()),
					oreLocation, materialLocation, material.getType() != MaterialType.DUST ? 2 : 5, 1F);
		}
	}
}
