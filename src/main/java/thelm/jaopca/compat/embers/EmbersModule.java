package thelm.jaopca.compat.embers;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import teamroots.embers.ConfigManager;
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

@JAOPCAModule(modDependencies = "embers@[1,)")
public class EmbersModule implements IModule {

	public static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Copper", "Gold", "Iron", "Lead", "Nickel", "Silver", "Tin"));

	private static boolean jaopcaOnly = false;

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
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		EmbersHelper helper = EmbersHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		int amount = ConfigManager.melterOreAmount*2;
		for(IMaterial material : moduleData.getMaterials()) {
			if(!jaopcaOnly || moltenMaterials.contains(material)) {
				String oreOredict = miscHelper.getOredictName("ore", material.getName());
				String moltenName = miscHelper.getFluidName("", material.getName());
				if(material.hasExtra(1)) {
					String extraMoltenName = miscHelper.getFluidName("", material.getExtra(1).getName());
					helper.registerMeltingRecipe(
							miscHelper.getRecipeKey("embers.ore_to_molten", material.getName()),
							oreOredict, moltenName, amount, extraMoltenName, 16);
				}
				else {
					helper.registerMeltingRecipe(
							miscHelper.getRecipeKey("embers.ore_to_molten", material.getName()),
							oreOredict, moltenName, amount);
				}
			}
		}
	}
}
