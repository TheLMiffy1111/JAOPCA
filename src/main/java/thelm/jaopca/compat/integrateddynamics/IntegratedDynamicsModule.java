package thelm.jaopca.compat.integrateddynamics;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

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

@JAOPCAModule(modDependencies = "integrateddynamics")
public class IntegratedDynamicsModule implements IModule {

	static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Ardite", "Coal", "Cobalt", "Copper", "Dark", "Diamond", "Emerald", "Gold", "Iron", "Lapis", "Lead",
			"Mithril", "Nickel", "Platinum", "Quartz", "Redstone", "Silver", "Tin"));

	@Override
	public String getName() {
		return "integrateddynamics";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
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
		JAOPCAApi api = ApiImpl.INSTANCE;
		IntegratedDynamicsHelper helper = IntegratedDynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			helper.registerSqueezerRecipe(
					miscHelper.getRecipeKey("integrateddynamics.ore_to_dust", material.getName()),
					oreOredict, new Object[] {
							dustOredict, 1, 1F,
							dustOredict, 1, 0.75F,
					});
			helper.registerMechanicalSqueezerRecipe(
					miscHelper.getRecipeKey("integrateddynamics.ore_to_dust_mechanical", material.getName()),
					oreOredict, new Object[] {
							dustOredict, 2, 1F,
							dustOredict, 1, 0.5F,
					}, 40);
		}
	}
}
