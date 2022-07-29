package thelm.jaopca.compat.assemblylinemachines;

import java.util.Arrays;
import java.util.EnumSet;
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

@JAOPCAModule(modDependencies = "assemblylinemachines")
public class AssemblyLineMachinesModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminium", "aluminum", "amethyst", "ardite", "brass", "bronze", "chromium", "coal", "cobalt",
			"constantan", "copper", "diamond", "electrum", "emerald", "flerovium", "gold", "invar", "iron",
			"lapis", "lead", "manyullyn", "netherite", "netherite_scrap", "nickel", "osmium", "platinum",
			"rose_gold", "silver", "tin", "titanium", "tungsten", "uranium", "zinc"));

	@Override
	public String getName() {
		return "assemblylinemachines";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT, MaterialType.INGOT_LEGACY, MaterialType.GEM, MaterialType.CRYSTAL);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		AssemblyLineMachinesHelper helper = AssemblyLineMachinesHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			helper.registerGrinderRecipe(
					new ResourceLocation("jaopca", "assemblylinemachines.ore_to_dust."+material.getName()),
					oreLocation, dustLocation, 2, 10, 2, false, 0F);
			if(material.getType() == MaterialType.INGOT) {
				ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
				ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
				helper.registerGrinderRecipe(
						new ResourceLocation("jaopca", "assemblylinemachines.raw_material_to_dust."+material.getName()),
						rawMaterialLocation, dustLocation, 1, 5, 2, false, 0.25F);
				if(itemTags.contains(rawStorageBlockLocation)) {
					helper.registerGrinderRecipe(
							new ResourceLocation("jaopca", "assemblylinemachines.raw_storage_block_to_dust."+material.getName()),
							rawStorageBlockLocation, dustLocation, 9, 10, 2, false, 0.25F);
				}
			}
		}
	}
}
