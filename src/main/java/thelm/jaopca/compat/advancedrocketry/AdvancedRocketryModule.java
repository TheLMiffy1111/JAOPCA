package thelm.jaopca.compat.advancedrocketry;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "advancedrocketry@[1.12.2-2,)")
public class AdvancedRocketryModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Copper", "Gold", "Iridium", "Iron", "Tin", "Titanium"));

	@Override
	public String getName() {
		return "advancedrocketry";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
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
		AdvancedRocketryHelper helper = AdvancedRocketryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			helper.registerSmallPlatePressRecipe(
					miscHelper.getRecipeKey("advancedrocketry.ore_to_dust", material.getName()),
					oreOredict, dustOredict, 2);
		}
	}
}
