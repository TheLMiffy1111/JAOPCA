package thelm.jaopca.compat.appliedenergistics2;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import appeng.core.AEConfig;
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

@JAOPCAModule(modDependencies = "appliedenergistics2")
public class AppliedEnergistics2Module implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>();

	@Override
	public String getName() {
		return "appliedenergistics2";
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
		if(BLACKLIST.isEmpty()) {
			Collections.addAll(BLACKLIST, AEConfig.instance().getGrinderOres());
		}
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		AppliedEnergistics2Helper helper = AppliedEnergistics2Helper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		float chance = (float)(AEConfig.instance().getOreDoublePercentage()/100);
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			helper.registerGrinderRecipe(
					miscHelper.getRecipeKey("appliedenergistics2.ore_to_dust", material.getName()),
					oreOredict, dustOredict, 1, dustOredict, 1, chance, 8);
		}
	}
}
