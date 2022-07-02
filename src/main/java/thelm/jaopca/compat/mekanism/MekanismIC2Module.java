package thelm.jaopca.compat.mekanism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.compat.ic2.IC2Helper;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = {"mekanism", "ic2"})
public class MekanismIC2Module implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Copper", "Gold", "Iron", "Lead", "Osmium", "Silver", "Tin"));
	private static Set<String> configToDirtyDustBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "mekanism_ic2";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDirtyDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have macerator to dirty dust recipes added."),
				configToDirtyDustBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IC2Helper helper = IC2Helper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type == MaterialType.INGOT && !BLACKLIST.contains(name) && !configToDirtyDustBlacklist.contains(name)) {
				String clumpOredict = miscHelper.getOredictName("clump", material.getName());
				String dirtyDustOredict = miscHelper.getOredictName("dustDirty", material.getName());
				if(oredict.contains(clumpOredict) && oredict.contains(dirtyDustOredict)) {
					helper.registerMaceratorRecipe(
							miscHelper.getRecipeKey("mekanism_ic2.clump_to_dirty_dust", material.getName()),
							clumpOredict, 1, dirtyDustOredict, 1);
				}
			}
		}
	}
}
