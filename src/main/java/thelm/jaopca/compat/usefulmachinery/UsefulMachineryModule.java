package thelm.jaopca.compat.usefulmachinery;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "usefulmachinery")
public class UsefulMachineryModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "copper", "gold", "iron", "lead", "nickel", "platinum", "silver", "tin", "uranium"));

	@Override
	public String getName() {
		return "usefulmachinery";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(1, "dusts");
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
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		UsefulMachineryHelper helper = UsefulMachineryHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			if(material.hasExtra(1)) {
				IMaterial extraMaterial = material.getExtra(1);
				ResourceLocation extraLocation = miscHelper.getTagLocation(extraMaterial.getType() == MaterialType.INGOT ? "raw_materials" : "dusts", extraMaterial.getName());
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "usefulmachinery.ore_to_raw_material."+material.getName()),
						oreLocation, rawMaterialLocation, 2, extraLocation, 1, 0.1F, 200, true, true);
			}
			else {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "usefulmachinery.ore_to_raw_material."+material.getName()),
						oreLocation, rawMaterialLocation, 2, 200, true, true);
			}
		}
	}
}
