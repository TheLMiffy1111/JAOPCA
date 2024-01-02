package thelm.jaopca.compat.wtbwmachines;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

@JAOPCAModule(modDependencies = "wtbw_machines")
public class WTBWMachinesCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"charcoal", "coal", "diamond", "emerald", "gold", "iron", "quartz"));
	private static final Set<String> TO_PLATE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"gold", "iron"));

	private static final TreeMap<IMaterial, Object> ORE_BASES = new TreeMap<>();
	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "wtbw_machines_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		WTBWMachinesHelper helper = WTBWMachinesHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(material.getName())) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
				if(itemTags.contains(dustLocation)) {
					helper.registerCrushingRecipe(
							new ResourceLocation("jaopca", "wtbw_machines.material_to_dust."+material.getName()),
							materialLocation, 1, 180, 2500, new Object[] {
									dustLocation, 1,
							});
				}
			}
			if(type.isIngot() && !TO_PLATE_BLACKLIST.contains(material.getName())) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation plateLocation = miscHelper.getTagLocation("plates", material.getName());
				if(itemTags.contains(plateLocation)) {
					helper.registerCompressingRecipe(
							new ResourceLocation("jaopca", "wtbw_machines.material_to_plate."+material.getName()),
							materialLocation, 1, plateLocation, 1, 180, 2500);
				}
			}
		}
	}
}
