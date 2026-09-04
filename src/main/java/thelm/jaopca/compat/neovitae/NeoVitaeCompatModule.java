package thelm.jaopca.compat.neovitae;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.Identifier;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "neovitae")
public class NeoVitaeCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "hellforged", "iron", "netherite_scrap"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "neovitae_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have ARC to dust recipes added."),
				configToDustBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		NeoVitaeHelper helper = NeoVitaeHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Identifier cuttingFluidLocation = Identifier.parse("neovitae:athanor_tool/cutting_fluids");
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				Identifier materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				Identifier dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(api.getItemTags().contains(dustLocation)) {
					helper.registerAthanorRecipe(
							miscHelper.getRecipeKey("neovitae.material_to_dust", material.getName()),
							materialLocation, 1, cuttingFluidLocation, new Object[] {
									dustLocation, 1,
							}, Map.of(), false);
				}
			}
		}
	}
}
