package thelm.jaopca.compat.energizedpower;

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

@JAOPCAModule(modDependencies = "energizedpower")
public class EnergizedPowerModule implements IModule {

	private static final double[] ORE_CHANCES = {1, 1, 0.25};
	private static final double[] ORE_CHANCES_ADVANCED = {1, 1, 0.5, 0.25};
	private static final double[] RAW_CHANCES = {1, 0.25};
	private static final double[] RAW_CHANCES_ADVANCED = {1, 0.5};
	private static final double[] RAW_BLOCK_CHANCES = {1, 1, 1, 1, 1, 1, 1, 1, 1, 0.5, 0.5, 0.25};
	private static final double[] RAW_BLOCK_CHANCES_ADVANCED = {1, 1, 1, 1, 1, 1, 1, 1, 1, 0.75, 0.5, 0.25, 0.25};
	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"copper", "gold", "iron", "netherite", "netherite_scrap", "tin"));

	@Override
	public String getName() {
		return "energizedpower";
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
		EnergizedPowerHelper helper = EnergizedPowerHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerPulverizerRecipe(
					new ResourceLocation("jaopca", "energizedpower.ore_to_dust."+material.getName()),
					oreLocation, dustLocation, ORE_CHANCES, ORE_CHANCES_ADVANCED);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				helper.registerPulverizerRecipe(
						new ResourceLocation("jaopca", "energizedpower.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, dustLocation, RAW_CHANCES, RAW_CHANCES_ADVANCED);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerPulverizerRecipe(
							new ResourceLocation("jaopca", "energizedpower.raw_storage_block_to_dust."+material.getName()),
							rawStorageBlockLocation, dustLocation, RAW_BLOCK_CHANCES, RAW_BLOCK_CHANCES_ADVANCED);
				}
			}
		}
	}
}
