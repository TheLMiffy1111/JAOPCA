package thelm.jaopca.compat.enderio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import cpw.mods.fml.common.event.FMLInitializationEvent;
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

@JAOPCAModule(modDependencies = "EnderIO")
public class EnderIOCompatModule implements IModule {

	private static final Set<String> TO_DUST_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Adamantine", "Aluminium", "AluminiumBrass", "Aluminum", "AluminumBrass", "Amordrine", "Angmallen",
			"Ardite", "AstralSilver", "Atlarus", "BlackSteel", "Blutonium", "Brass", "Bronze", "Bronze", "Carmot",
			"Celenegil", "CertusQuartz", "Ceruclase", "Cobalt", "Copper", "Cyanite", "DamascusSteel", "DeepIron",
			"Electrum", "Enderium", "Fluix", "Gold", "Graphite", "Haderoth", "Hepatizon", "Ignatius", "Infuscolium",
			"Inolashite", "Invar", "Iron", "Kalendrite", "Lapis", "Lead", "Lemurite", "Ludicrite", "Lumium",
			"Manganese", "Manyullyn", "Midasium", "Mithril", "NaturalAluminum", "NetherQuartz", "Nickel", "Orichalcum",
			"Osmium", "Oureclase", "Platinum", "Prometheum", "Quartz", "Quicksilver", "Rubracium", "Sanguinite",
			"ShadowIron", "ShadowSteel", "Signalum", "Silver", "Steel", "Tartarite", "Tin", "Vulcanite", "Vyroxeres",
			"Yellorite", "Yellorium", "Zinc"));
	private static Set<String> configMaterialToDustBlacklist = new TreeSet<>();
	private static Set<String> configBlockToDustBlacklist = new TreeSet<>();

	@Override
	public String getName() {
		return "enderio_compat";
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
						helper.configMaterialPredicate(), "The materials that should not have sagmill material to dust recipes added."),
				configMaterialToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.blockToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have sagmill block to dust recipes added."),
				configBlockToDustBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		EnderIOHelper helper = EnderIOHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !TO_DUST_BLACKLIST.contains(name) && !configMaterialToDustBlacklist.contains(name)) {
				String materialOredict = miscHelper.getOredictName(type.getFormName(), name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(dustOredict)) {
					helper.registerSagMillRecipe(
							miscHelper.getRecipeKey("enderio.material_to_dust", name),
							materialOredict, 2400, "none", new Object[] {
									dustOredict, 1, 1F,
							});
				}
			}
			if(type.isIngot() && !TO_DUST_BLACKLIST.contains(name) && !configBlockToDustBlacklist.contains(name)) {
				String blockOredict = miscHelper.getOredictName("block", name);
				String dustOredict = miscHelper.getOredictName("dust", name);
				if(oredict.contains(dustOredict)) {
					helper.registerSagMillRecipe(
							miscHelper.getRecipeKey("enderio.block_to_dust", name),
							blockOredict, 3600, "none", new Object[] {
									dustOredict, material.isSmallStorageBlock() ? 4 : 9, 1F,
							});
				}
			}
		}
	}
}
