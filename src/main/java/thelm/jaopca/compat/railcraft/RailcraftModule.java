package thelm.jaopca.compat.railcraft;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.fml.common.Loader;
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

@JAOPCAModule(modDependencies = "railcraft")
public class RailcraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>();

	static {
		if(Loader.isModLoaded("ic2")) {
			Collections.addAll(BLACKLIST, "Copper", "Gold", "Iron", "Lead", "Tin", "Silver", "Uranium");
		}
	}

	@Override
	public String getName() {
		return "railcraft";
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
		JAOPCAApi api = ApiImpl.INSTANCE;
		RailcraftHelper helper = RailcraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String crushedOredict = miscHelper.getOredictName("crushed", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			if(oredict.contains(crushedOredict)) {
				helper.registerRockCrusherRecipe(
						miscHelper.getRecipeKey("railcraft.ore_to_crushed", material.getName()),
						oreOredict, 100, new Object[] {
								crushedOredict, 2, 1F,
						});
			}
			else {
				helper.registerRockCrusherRecipe(
						miscHelper.getRecipeKey("railcraft.ore_to_dust", material.getName()),
						oreOredict, 100, new Object[] {
								dustOredict, 2, 1F,
						});
			}
		}
	}
}
