package thelm.jaopca.compat.integrateddynamics;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "integrateddynamics")
public class IntegratedDynamicsModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"ardite", "coal", "cobalt", "copper", "diamond", "emerald", "gold", "iron", "lapis", "lead",
			"mithril", "nickel", "platinum", "quartz", "silver", "tin"));

	@Override
	public String getName() {
		return "integrateddynamics";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.GEM, MaterialType.CRYSTAL);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		IntegratedDynamicsHelper helper = IntegratedDynamicsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerSqueezerRecipe(
					new ResourceLocation("jaopca", "integrateddynamics.ore_to_dust."+material.getName()),
					oreLocation, new Object[] {
							dustLocation, 2, 1F,
							dustLocation, 1, 0.75F,
					});
			helper.registerMechanicalSqueezerRecipe(
					new ResourceLocation("jaopca", "integrateddynamics.ore_to_dust_mechanical."+material.getName()),
					oreLocation, new Object[] {
							dustLocation, 3, 1F,
							dustLocation, 1, 0.5F,
							dustLocation, 1, 0.5F,
					}, 40);
		}
	}
}
