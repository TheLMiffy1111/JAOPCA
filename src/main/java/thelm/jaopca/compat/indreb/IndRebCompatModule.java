package thelm.jaopca.compat.indreb;

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

@JAOPCAModule(modDependencies = "indreb")
public class IndRebCompatModule implements IModule {

	private static final Set<String> TO_BLOCK_BLACKLIST = new TreeSet<>(List.of(
			"bone", "brick", "bronze", "coal", "copper", "diamond", "emerald", "glowstone", "gold", "iron", "lapis",
			"nether_brick", "netherite", "quartz", "redstone", "silver", "steel", "tin"));
	private static Set<String> configToBlockBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "indreb_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toStorageBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor to storage block recipes added."),
				configToBlockBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IndRebHelper helper = IndRebHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String name = material.getName();
			if(!TO_BLOCK_BLACKLIST.contains(name) && !configToBlockBlacklist.contains(name)) {
				ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
				ResourceLocation storageBlockLocation = miscHelper.getTagLocation("storage_blocks", material.getName());
				if(api.getItemTags().contains(storageBlockLocation)) {
					helper.registerCompressingRecipe(
							new ResourceLocation("jaopca", "indreb.material_to_storage_block."+material.getName()),
							materialLocation, material.isSmallStorageBlock() ? 4 : 9,
							storageBlockLocation, 1,
							180, 8, 0.3F);
				}
			}
		}
	}
}
