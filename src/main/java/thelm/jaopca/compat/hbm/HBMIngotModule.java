package thelm.jaopca.compat.hbm;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "hbm@[1.0.27_X4515,)", classDependencies = "com.hbm.main.MainRegistry")
public class HBMIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Beryllium", "Coal", "Cobalt", "Copper", "Gold", "Hematite", "Iron", "Lead",
			"Malachite", "NaturalAluminum", "Redstone", "Thorium232", "Titanium", "Tungsten", "Uranium"));

	@Override
	public String getName() {
		return "hbm_ingot";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "hbm_material");
		builder.put(1, "hbm_material");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		HBMHelper helper = HBMHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			if(material.hasExtra(1) && material.getExtra(1).getType().isIngot()) {
				helper.registerCrucibleSmeltingRecipe(
						miscHelper.getRecipeKey("hbm.ore_to_ntm_material", material.getName()),
						oreOredict, new Object[] {
								material.getName(), MaterialShapes.INGOT.q(2),
								material.getExtra(1).getName(), MaterialShapes.NUGGET.q(3),
								Mats.MAT_STONE, MaterialShapes.QUART.q(1),
						});
			}
			else {
				helper.registerCrucibleSmeltingRecipe(
						miscHelper.getRecipeKey("hbm.ore_to_ntm_material", material.getName()),
						oreOredict, new Object[] {
								material.getName(), MaterialShapes.INGOT.q(2),
								Mats.MAT_STONE, MaterialShapes.QUART.q(1),
						});
			}
		}
	}
}
