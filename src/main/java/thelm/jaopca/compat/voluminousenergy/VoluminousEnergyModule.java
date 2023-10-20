package thelm.jaopca.compat.voluminousenergy;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "voluminousenergy@[1.18.2J9-0.4.0.0,),[1.18.2JX-0.4.0.0,)")
public class VoluminousEnergyModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"eighzo", "copper", "gold", "iron", "netherite", "netherite_scrap"));

	@Override
	public String getName() {
		return "voluminousenergy";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		VoluminousEnergyHelper helper = VoluminousEnergyHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "voluminousenergy.ore_to_dust."+material.getName()),
					oreLocation, 1, dustLocation, 2, 200, 1, 3);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "voluminousenergy.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, 1, dustLocation, 1, dustLocation, 1, 0.333F, 200, 1, 3);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerCrushingRecipe(
							new ResourceLocation("jaopca", "voluminousenergy.raw_storage_block_to_dust."+material.getName()),
							rawStorageBlockLocation, 1, dustLocation, 9, dustLocation, 9, 0.333F, 200, 1, 3);
				}
			}
		}
	}
}
