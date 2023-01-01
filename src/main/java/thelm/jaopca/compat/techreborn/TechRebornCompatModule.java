package thelm.jaopca.compat.techreborn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import techreborn.items.ingredients.ItemDusts;
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

@JAOPCAModule(modDependencies = "techreborn")
public class TechRebornCompatModule implements IModule {

	private static final Set<String> TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Amethyst", "Apatite", "CertusQuartz", "Diamond", "Emerald", "Malachite", "Peridot", "RedGarnet",
			"Ruby", "Sapphire", "Tanzanite", "Topaz", "YellowGarnet"));
	private static Set<String> configMaterialToDustBlacklist = new TreeSet<>();
	private static Set<String> configPlateToDustBlacklist = new TreeSet<>();
	private static Set<String> configBlockToDustBlacklist = new TreeSet<>();
	private static Set<String> configCrystalToPlateBlacklist = new TreeSet<>();
	private static Set<String> configDustToPlateBlacklist = new TreeSet<>();
	private static Set<String> configToCrystalBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "techreborn_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.materialToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have grinder material to dust recipes added."),
				configMaterialToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.plateToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have grinder plate to dust recipes added."),
				configPlateToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have grinder block to dust recipes added."),
				configBlockToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.crystalToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor material to plate recipes added."),
				configCrystalToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.dustToPlateMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have compressor dust to plate recipes added."),
				configDustToPlateBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.toCrystalMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have implosion compressor to material recipes added."),
				configToCrystalBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TechRebornHelper helper = TechRebornHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		ItemStack darkAshes = ItemDusts.getDustByName("dark_ashes", 1);
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !configMaterialToDustBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(dustOredict) && !containsTechRebornItem(dustOredict)) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("techreborn.material_to_dust", name),
							materialOredict, 1, dustOredict, 1, 300, 2);
				}
			}
			if(!configPlateToDustBlacklist.contains(name)) {
				String plateOredict = miscHelper.getOredictName("plate", name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(plateOredict) && oredict.contains(dustOredict) && !containsTechRebornItem(dustOredict)) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("techreborn.plate_to_dust", name),
							plateOredict, 1, dustOredict, 1, 300, 2);
				}
			}
			if(!configBlockToDustBlacklist.contains(name)) {
				String blockOredict = miscHelper.getOredictName("block", name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(blockOredict) && oredict.contains(dustOredict) && !containsTechRebornItem(dustOredict)) {
					helper.registerGrinderRecipe(
							miscHelper.getRecipeKey("techreborn.block_to_dust", name),
							blockOredict, 1, dustOredict, material.isSmallStorageBlock() ? 4 : 9, 300, 2);
				}
			}
			if(type.isCrystalline() && !configCrystalToPlateBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(plateOredict) && !containsTechRebornItem(plateOredict)) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("techreborn.material_to_plate", name),
							materialOredict, 1, plateOredict, 1, 400, 2);
				}
			}
			if(!type.isIngot() && !configDustToPlateBlacklist.contains(name)) {
				String dustOredict = miscHelper.getOredictName("dust", name);
				String plateOredict = miscHelper.getOredictName("plate", name);
				if(oredict.contains(dustOredict) && oredict.contains(plateOredict) && (type.isDust() || !containsTechRebornItem(plateOredict))) {
					helper.registerCompressorRecipe(
							miscHelper.getRecipeKey("techreborn.dust_to_plate", name),
							dustOredict, 1, plateOredict, 1, 400, 2);
				}
			}
			if(type.isCrystalline() && !TO_CRYSTAL_BLACKLIST.contains(name) && !configToCrystalBlacklist.contains(name)) {
				String dustOredict = miscHelper.getOredictName("dust", name);
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				if(oredict.contains(dustOredict)) {
					helper.registerImplosionCompressorRecipe(
							miscHelper.getRecipeKey("techreborn.dust_to_material", name),
							new Object[] {
									dustOredict, 4, Blocks.TNT, 16,
							}, new Object[] {
									materialOredict, 3, darkAshes, 12,
							}, 20, 32);
				}
			}
		}
	}

	public boolean containsTechRebornItem(String oredict) {
		return OreDictionary.getOres(oredict, false).stream().
				anyMatch(s->s.getItem().getRegistryName().getNamespace().equals("techreborn"));
	}
}
