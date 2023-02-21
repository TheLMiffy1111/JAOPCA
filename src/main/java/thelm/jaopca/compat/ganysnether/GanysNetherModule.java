package thelm.jaopca.compat.ganysnether;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

/**
 * Although Gany's Nether does not contain ore processing, AOBD added support
 * and therefore this module replicates the behavior.
 */
@JAOPCAModule(modDependencies = "ganysnether")
public class GanysNetherModule implements IModule {

	@Override
	public String getName() {
		return "ganysnether";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(1, "nugget");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		GanysNetherHelper helper = GanysNetherHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			String extraNuggetOredict = miscHelper.getOredictName("nugget", material.getExtra(1).getName());
			helper.registerMagmaticCentrifugeRecipe(
					miscHelper.getRecipeKey("ganysnether.ore_to_material", material.getName()),
					oreOredict, 1, oreOredict, 1, new Object[] {
							materialOredict, 1,
							materialOredict, 1,
							materialOredict, 1,
							extraNuggetOredict, 1,
					});
		}
	}
}
