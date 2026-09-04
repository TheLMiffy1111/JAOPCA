package thelm.jaopca.compat.oritech;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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

@JAOPCAModule(modDependencies = "oritech")
public class OritechCompatModule implements IModule {

	private static final Set<String> PULVERIZER_DUST_BLACKLIST = new TreeSet<>(List.of(
			"adamant", "biosteel", "coal", "copper", "duratium", "electrum", "ender_pearl", "energite",
			"gold", "iron", "nickel", "platinum", "quartz", "steel"));
	private static final Set<String> FRAGMENT_FORGE_DUST_BLACKLIST = new TreeSet<>(List.of(
			"adamant", "biosteel", "coal", "copper", "duratium", "electrum", "ender_pearl", "energite",
			"gold", "iron", "nickel", "platinum", "quartz", "steel"));
	private static Set<String> configPulverizerToDustBlacklist = new TreeSet<>();
	private static Set<String> configFragmentForgeToDustBlacklist = new TreeSet<>();

	static {
		if(ModList.get().isLoaded("energizedpower")) {
			Collections.addAll(PULVERIZER_DUST_BLACKLIST, "tin");
			Collections.addAll(FRAGMENT_FORGE_DUST_BLACKLIST, "tin");
		}
		if(ModList.get().isLoaded("techreborn")) {
			Collections.addAll(PULVERIZER_DUST_BLACKLIST, "aluminum", "aluminium", "brass", "bronze", "chromium",
					"invar", "peridot", "red_garnet", "ruby", "sapphire", "titanium", "yellow_garnet");
		}
	}

	@Override
	public String getName() {
		return "oritech_compat";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		IMiscHelper helper = MiscHelper.INSTANCE;
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.pulverizerToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have pulverizer to dust recipes added."),
				configPulverizerToDustBlacklist);
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.fragmentForgeToDustMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have fragment forge to dust recipes added."),
				configFragmentForgeToDustBlacklist);
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		OritechHelper helper = OritechHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<Identifier> itemTags = api.getItemTags();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !PULVERIZER_DUST_BLACKLIST.contains(name) && !configPulverizerToDustBlacklist.contains(name)) {
				Identifier materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				Identifier dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerPulverizerRecipe(
							miscHelper.getRecipeKey("oritech.material_to_dust_pulverizer", name),
							materialLocation, dustLocation, 1, 200);
				}
			}
			if(!type.isDust() && !FRAGMENT_FORGE_DUST_BLACKLIST.contains(name) && !configFragmentForgeToDustBlacklist.contains(name)) {
				Identifier materialLocation = miscHelper.getTagLocation(type.getFormName(), name);
				Identifier dustLocation = miscHelper.getTagLocation("dusts", name);
				if(itemTags.contains(dustLocation)) {
					helper.registerFragmentForgeRecipe(
							miscHelper.getRecipeKey("oritech.material_to_dust_fragment_forge", name),
							materialLocation, dustLocation, 1, 140);
				}
			}
		}
	}
}
