package thelm.jaopca.compat.create;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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

@JAOPCAModule(modDependencies = "create")
public class CreateCompatModule implements IModule {

	private static final Set<String> MATERIAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"AdvancedAlloy", "Alubrass", "Aluminion", "Aluminum", "Amirite", "Ardite", "Astolfite", "Beryllium", "Brass",
			"Chinesium989", "Cobalt", "Constantan", "Copper", "Creativesteel", "Doxxium", "Electrum", "Gold", "Iron",
			"Knightslime", "Lead", "Manyullyn", "Mingrade", "Nickel", "Nikonium", "Osmium", "Pigiron", "Platinum",
			"Quicksilver", "Saturnite", "Schrabidium", "Silver", "Stalinium", "Steel", "Tin", "Titanium", "Tungsten",
			"Uranium", "Zinc"));
	private static final Set<String> BLOCK_BLACKLIST = new TreeSet<>(Arrays.asList(
			"AdvancedAlloy", "Alubrass", "Aluminion", "Aluminum", "Amirite", "Ardite", "Astolfite", "Beryllium", "Bone",
			"Brass", "Brick", "BrickNether", "Chinesium989", "Clay", "Coal", "Cobalt", "Constantan", "Copper", "Creativesteel",
			"Doxxium", "Electrum", "Emerald", "Glowstone", "Gold", "Iron", "Knightslime", "Lapis", "Lead", "Manyullyn",
			"Mingrade", "Nickel", "Nikonium", "Osmium", "Pigiron", "Platinum", "Prismarine", "Quicksilver", "Redstone",
			"Saturnite", "Schrabidium", "Silver", "Stalinium", "Steel", "Tin", "Titanium", "Tungsten", "Uranium", "Zinc"));
	private static Set<String> configToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToMaterialBlacklist = new TreeSet<>();
	private static Set<String> configToBlockBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "create_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have press to plate recipes added."),
				configToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toMaterialMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compacting to material recipes added."),
				configToMaterialBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toBlockMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compacting to block recipes added."),
				configToBlockBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		CreateHelper helper = CreateHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(type.isIngot() && !MATERIAL_BLACKLIST.contains(name) && !configToPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(plateOredict)) {
					helper.registerPressingRecipe(
							miscHelper.getRecipeKey("create.material_to_plate", name),
							materialOredict, plateOredict, 1);
				}
			}
			if(!type.isDust() && !MATERIAL_BLACKLIST.contains(name) && !configToMaterialBlacklist.contains(name)) {
				String nuggetOredict = miscHelper.getOredictName("nugget", name);
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				if(oredict.contains(nuggetOredict)) {
					helper.registerCompactingRecipe(
							miscHelper.getRecipeKey("create.nugget_to_material", name),
							nuggetOredict, 9, materialOredict, 1, 0);
				}
			}
			if(!BLOCK_BLACKLIST.contains(name) && !configToBlockBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String blockOredict = miscHelper.getOredictName("block", name);
				if(oredict.contains(blockOredict)) {
					helper.registerCompactingRecipe(
							miscHelper.getRecipeKey("create.nugget_to_material", name),
							materialOredict, material.isSmallStorageBlock() ? 4 : 9, blockOredict, 1, 0);
				}
			}
		}
	}
}
