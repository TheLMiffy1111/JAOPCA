package thelm.jaopca.compat.ic2;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
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

@JAOPCAModule(modDependencies = "ic2@[1.19.2-2.0.9,1.20)")
public class IC2CompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(List.of(
			"aluminium", "aluminum", "charcoal", "coal", "copper", "diamond", "gold", "iron", "silver", "tin"));
	private static Set<String> configToDustBlacklist = new TreeSet<>();
	private static Set<String> configStorageBlockToDustBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "ic2_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have macerator to dust recipes added."),
				configToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.storagBlockToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have macerator storage block to dust recipes added."),
				configStorageBlockToDustBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IC2Helper helper = IC2Helper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<ResourceLocation> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configToDustBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerMaceratorRecipe(
							new ResourceLocation("jaopca", "ic2.material_to_dust."+material.getName()),
							materialLocation, 1,
							dustLocation, 1,
							1, 1, 0.1F);
				}
			}
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configStorageBlockToDustBlacklist.contains(name)) {
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", name);
				ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(storageBlockLocation) && itemTags.contains(dustLocation)) {
					helper.registerMaceratorRecipe(
							new ResourceLocation("jaopca", "ic2.storage_block_to_dust."+material.getName()),
							storageBlockLocation, 1,
							dustLocation, material.isSmallStorageBlock() ? 4 : 9,
							4, 1, 0.9F);
				}
			}
		}
	}
}
