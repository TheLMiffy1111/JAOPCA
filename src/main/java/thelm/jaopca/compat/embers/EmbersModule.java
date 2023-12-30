package thelm.jaopca.compat.embers;

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

@JAOPCAModule(modDependencies = "embers")
public class EmbersModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "copper", "gold", "iron", "lead", "nickel", "silver", "tin"));

	@Override
	public String getName() {
		return "embers";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(0, "molten");
		builder.put(1, "molten");
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
		EmbersHelper helper = EmbersHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
			ResourceLocation rawStorageBlockLocation = miscHelper.getTagLocation("storage_blocks/raw", material.getName(), "_");
			ResourceLocation moltenLocation = miscHelper.getTagLocation("molten", material.getName(), "_");

			if(material.hasExtra(1)) {
				ResourceLocation extraMoltenLocation = miscHelper.getTagLocation("molten", material.getExtra(1).getName(), "_");

				helper.registerMeltingRecipe(
						new ResourceLocation("jaopca", "embers.ore_to_molten."+material.getName()),
						oreLocation, moltenLocation, 240, extraMoltenLocation, 20);
				if(material.getType() == MaterialType.INGOT) {
					helper.registerMeltingRecipe(
							new ResourceLocation("jaopca", "embers.raw_material_to_molten."+material.getName()),
							rawMaterialLocation, moltenLocation, 120, extraMoltenLocation, 10);
					if(itemTags.contains(rawStorageBlockLocation)) {
						helper.registerMeltingRecipe(
								new ResourceLocation("jaopca", "embers.raw_storage_block_to_molten."+material.getName()),
								rawStorageBlockLocation, moltenLocation, 1080, extraMoltenLocation, 90);
					}
				}
			}
			else {
				helper.registerMeltingRecipe(
						new ResourceLocation("jaopca", "embers.ore_to_molten."+material.getName()),
						oreLocation, moltenLocation, 240);
				if(material.getType() == MaterialType.INGOT) {
					helper.registerMeltingRecipe(
							new ResourceLocation("jaopca", "embers.raw_material_to_molten."+material.getName()),
							rawMaterialLocation, moltenLocation, 120);
					if(itemTags.contains(rawStorageBlockLocation)) {
						helper.registerMeltingRecipe(
								new ResourceLocation("jaopca", "embers.raw_storage_block_to_molten."+material.getName()),
								rawStorageBlockLocation, moltenLocation, 1080);
					}
				}
			}
		}
	}
}
