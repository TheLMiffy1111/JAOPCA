package thelm.jaopca.compat.rotarycraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

import Reika.RotaryCraft.Registry.ConfigRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fluids.FluidRegistry;
import tconstruct.library.crafting.FluidType;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.compat.tconstruct.TConstructHelper;
import thelm.jaopca.compat.tconstruct.TConstructModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = {"RotaryCraft", "TConstruct"})
public class RotaryCraftTConstructModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Ardite", "Coal", "Cobalt", "Copper", "Diamond", "Emerald", "Gold", "Iron",
			"Lapis", "Lead", "NaturalAluminum", "NetherQuartz", "Nickel", "Platinum", "Quartz", "Redstone",
			"Silver", "Tin"));
	private static Set<String> configFlakesToMoltenBlacklist = new TreeSet<>();

	private static boolean jaopcaOnly = true;

	@Override
	public String getName() {
		return "rotarycraft_tconstruct";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.allOf(MaterialType.class);
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		BLACKLIST.addAll(FluidType.fluidTypes.keySet());
		IMiscHelper helper = MiscHelper.INSTANCE;
		jaopcaOnly = config.getDefinedBoolean("recipes.jaopcaOnly", jaopcaOnly, "Should the module only add recipes for materials with JAOPCA molten fluids.");
		helper.caclulateMaterialSet(
				config.getDefinedStringList("recipes.flakesToMoltenMaterialBlacklist", new ArrayList<>(),
						helper.configMaterialPredicate(), "The materials that should not have flakes melting recipes added."),
				configFlakesToMoltenBlacklist);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		TConstructHelper helper = TConstructHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		Set<String> oredict = api.getOredict();
		Set<IMaterial> moltenMaterials = api.getForm("molten").getMaterials();
		for(IMaterial material : moduleData.getMaterials()) {
			MaterialType type = material.getType();
			String name = material.getName();
			if(!type.isDust() && !BLACKLIST.contains(name) && (!jaopcaOnly || moltenMaterials.contains(material))) {
				String moltenName = miscHelper.getFluidName(".molten", name);
				int baseAmount = material.getType().isIngot() ? 144 : 640;
				int baseTemp = TConstructModule.tempFunction.applyAsInt(material);
				if(FluidRegistry.isFluidRegistered(moltenName)) {
					if(!configFlakesToMoltenBlacklist.contains(name)) {
						String flakesOredict = miscHelper.getOredictName("RotaryCraft:flakes", name);
						String blockOredict = miscHelper.getOredictName("block", name);
						int amount = (int)Math.floor(baseAmount*ConfigRegistry.getSmelteryFlakeYield());
						if(oredict.contains(flakesOredict)) {
							helper.registerMeltingRecipe(
									miscHelper.getRecipeKey("rotarycraft_tconstruct.flakes_to_molten", name),
									flakesOredict, blockOredict, moltenName, amount, baseTemp);
						}
					}
				}
			}
		}
	}
}
